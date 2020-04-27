package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.google.gson.Gson
import com.snake.game.backend.Player
import com.snake.game.backend.SocketService
import com.snake.game.backend.Events
import com.snake.game.backend.PlayerEvent
import com.snake.game.backend.SimpleResponse
import com.snake.game.backend.DeleteEntities
import com.snake.game.controls.ItemPowerups
import com.snake.game.controls.InfoPane
import com.snake.game.controls.SwipeDetector
import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.PlayerComponent
import com.snake.game.ecs.component.ComponentTypeTree
import com.snake.game.ecs.entity.Entity
import org.json.JSONException
import org.json.JSONObject

class GameState(
        playerId: String, // The ID of the local player
        var players: MutableList<Player>, // The player in the room
        updatesBuffer: ArrayList<Array<Any>> // List of state updates received by the lobby
) : BaseState(Stage(ScreenViewport())) {

    // TODO It would be cleaner to get these dimensions from the server
    private val FIELD_WIDTH: Float = 500f
    private val FIELD_HEIGHT: Float = 300f

    private val SIDE_PANEL_SIZE = 0.2f

    private val ecs = SnakeECSEngine
    private val swipeDetector = SwipeDetector
    private val gameWidget = GameWidget(ecs, FIELD_WIDTH, FIELD_HEIGHT, getViewport())
    private val splitPane: SplitPane
    private val playersList = Table()
    private var infoPane: InfoPane
    private var itemPowerups: ItemPowerups


    init {
        ecs.localPlayerId = playerId

        cancelOldListeners()
        addListeners()

        // Apply updates that were received by the lobby
        updatesBuffer.forEach { args -> onStateUpdate(args) }

        val uiGroup = VerticalGroup()

        splitPane = SplitPane(uiGroup, gameWidget, false, skin)
        splitPane.setFillParent(true)
        splitPane.splitAmount = SIDE_PANEL_SIZE
        splitPane.minSplitAmount = SIDE_PANEL_SIZE
        splitPane.maxSplitAmount = SIDE_PANEL_SIZE

        val sidePanelWidth = Gdx.graphics.width * SIDE_PANEL_SIZE
        val sidePanelHeight = Gdx.graphics.height.toFloat()

        updatePlayersList()

        infoPane = InfoPane(uiGroup, sidePanelWidth, sidePanelHeight)
        itemPowerups = ItemPowerups(infoPane.width, infoPane.height)

        val backButton = createTextButton("lobby") {
            onBackPressed()
        }

        stage.addActor(splitPane)

        infoPane.addRow(playersList)
        infoPane.addRow(itemPowerups.getPowerupsControlPanel())
        infoPane.addBackButton(backButton)

        // we add swipe listener :
        swipeDetector.active = true
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE.value) { args ->
            // Gdx.app.log("SocketIO", "UPDATE " + args[0].toString())
            onStateUpdate(args)
        }.on(Events.DELETE_ENTITIES.value) { args ->
            Gdx.app.log("SocketIO", "DELETE_ENTITY")
            deleteEntities(args)
        }.on(Events.PLAYER_LEFT_GAME.value) { args ->
            Gdx.app.log("SocketIO", "PLAYER_LEFT_GAME")
            val data: JSONObject = args[0] as JSONObject
            val response: PlayerEvent = Gson().fromJson(data.toString(), PlayerEvent::class.java)
            playerLeftGame(response.id)
        }.on(Events.LEAVE_TO_LOBBY_RESPONSE.value) { args ->
            Gdx.app.log("SocketIO", "LEAVE_TO_LOBBY_RESPONSE")
            leaveToLobby(args)
        }
    }

    /**
     * Updates game component using data received from the server.
     * @param args JSON data from the server.
     */
    private fun onStateUpdate(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        try {
            val state = data.getJSONArray("state")

            // Update components
            for (i in 0 until state.length()) {
                val componentData = state.getJSONObject(i)
                val id: String = componentData.getString("entityId")
                val componentTypeName = componentData.getString("componentType")
                val componentType: ComponentType? = ComponentType.fromName(componentTypeName) ?: continue // skip if component type doesn't exist

                // Create the entity if necessary
                if (!ecs.entityManager.hasEntity(id))
                    Entity(id, ecs.entityManager)

                val entity = ecs.entityManager.getEntity(id)!!

                // Create the component if necessary
                if (!entity.hasComponent(componentType!!))
                    entity.addComponent(ComponentType.createComponent(componentType))

                // Update the component from the JSON data
                entity.getComponent(componentType)!!.updateFromJSON(componentData)
            }
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }

    private fun updatePlayersList() {
        playersList.clear()
        var playersEntities = ecs.entityManager.getEntities(ComponentTypeTree(ComponentType.Player))
        var playersComponents = mutableListOf<PlayerComponent>()

        for (p: Entity in playersEntities) {
            if (p.getComponent(ComponentType.Player) != null)
                playersComponents.add(p.getComponent(ComponentType.Player) as PlayerComponent)
        }

        for (player: Player in players) {
            var alive = false
            var invisible = false
            for (p: PlayerComponent in playersComponents) {
                if (p.playerId == player.id) {
                    alive = p.alive
                    invisible = p.invisible
                }
            }
            insertPlayer(player, alive, invisible)
        }
    }

    private fun insertPlayer(player: Player, alive: Boolean, invisible: Boolean = false) {
        val nicknameLabel = Label(player.nickname, skin, "title").apply {
            setSize(BaseState.ELEMENT_WIDTH * splitPane.splitAmount * 0.4F, ELEMENT_HEIGHT / 2)
        }

        val table = Table()
        var widthTotal = 0f
        var heightTotal = 0f

        if (!alive) {
            val aliveIcon = Image(Texture("indicators/dead-red.png")).apply {
                width = ELEMENT_WIDTH * splitPane.splitAmount * 0.15F
                height = width
            }

            table.add(aliveIcon).width(aliveIcon.width).height(aliveIcon.height).padRight(aliveIcon.width / 2)
            widthTotal += (aliveIcon.width + aliveIcon.width / 2)
            heightTotal += heightTotal.coerceAtLeast(aliveIcon.height)
        }

        if (invisible) {
            val invisibleIcon = Image(Texture("powerup-sprites/powerup-invisible.png")).apply {
                width = ELEMENT_WIDTH * splitPane.splitAmount * 0.15F
                height = width
            }

            table.add(invisibleIcon).width(invisibleIcon.width).height(invisibleIcon.height).padRight(invisibleIcon.width / 2)
            widthTotal += (invisibleIcon.width + invisibleIcon.width / 2)
            heightTotal += heightTotal.coerceAtLeast(invisibleIcon.height)
        }

        table.add(nicknameLabel).width(nicknameLabel.width).height(nicknameLabel.height)
        widthTotal += nicknameLabel.width
        heightTotal += heightTotal.coerceAtLeast(nicknameLabel.height)
        table.setSize(ELEMENT_WIDTH * splitPane.splitAmount * 0.5F, ELEMENT_HEIGHT / 2)
        playersList.add(table).width(table.width).height(table.height).padTop(nicknameLabel.height / 3f).expandX()
        playersList.row()
    }

    private fun checkPowerups() {
        val playersEntities = ecs.entityManager.getEntities(ComponentTypeTree(ComponentType.Player))
        val playersComponents = mutableListOf<PlayerComponent>()

        for (p: Entity in playersEntities) {
            if (p.getComponent(ComponentType.Player) != null)
                playersComponents.add(p.getComponent(ComponentType.Player) as PlayerComponent)
        }
        val player = playersComponents.find { player -> player.playerId == ecs.localPlayerId }
                ?: return
        itemPowerups.updateFb(player.fireballCount)
        itemPowerups.updateTw(player.throughWallsCount)
    }

    private fun checkWinner() {
        var alivePlayers = 0
        var deadPlayers = 0

        val playersEntities = ecs.entityManager.getEntities(ComponentTypeTree(ComponentType.Player))
        val playersComponents = mutableListOf<PlayerComponent>()

        for (p: Entity in playersEntities) {
            if (p.getComponent(ComponentType.Player) != null)
                playersComponents.add(p.getComponent(ComponentType.Player) as PlayerComponent)
        }
        for (p: PlayerComponent in playersComponents) {
            if (p.alive) {
                alivePlayers++
            } else {
                deadPlayers++
            }
        }
        val player = playersComponents.find { player -> player.playerId == ecs.localPlayerId }
                ?: return
        if (deadPlayers > 0 && alivePlayers == 1) {
            if (player.alive) {
                showButtonDialog("You won the game", "Ok"){
                    if (it == 0){
                        ecs.entityManager.clearEntities()
                        StateManager.pop()
                    }
                }
            }else{
               val winner=  players.find { p -> p.id == player.playerId  }
                showButtonDialog("${winner?.nickname} won the game", "Ok"){
                    if (it == 0){
                        ecs.entityManager.clearEntities()
                        StateManager.pop()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        showButtonDialog("Are you sure you want to return to the lobby?", "Yes", "No") {
            if (it == 0)
                SocketService.socket.emit(Events.LEAVE_TO_LOBBY.value)
        }
    }

    private fun leaveToLobby(args: Array<Any>) {
        ecs.entityManager.clearEntities()
        val data: JSONObject = args[0] as JSONObject
        val response: SimpleResponse = Gson().fromJson(data.toString(), SimpleResponse::class.java)
        Gdx.app.debug("UI", "GameState::leaveToLobby(%b)".format(response.success))
        if (response.success) {
            swipeDetector.active = false
            cancelNewListeners()
            StateManager.pop()
        } else {
            Gdx.app.log("UI", "::leaveToLobby Error: ${response.message}")
        }
    }

    private fun playerLeftGame(id: String) {
        Gdx.app.debug("UI", "GameState::playerLeftGame(%s)".format(id))
    }

    private fun deleteEntities(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val message: DeleteEntities = Gson().fromJson(data.toString(), DeleteEntities::class.java)

        for (entityId in message.entityIds)
            ecs.removeEntity(entityId)
    }

    private fun cancelNewListeners() {
        SocketService.socket.off(Events.PLAYER_LEFT_GAME.value)
        SocketService.socket.off(Events.LEAVE_TO_LOBBY_RESPONSE.value)
        SocketService.socket.off(Events.UPDATE.value)
    }

    private fun cancelOldListeners() {
        SocketService.socket.off(Events.OWNER_CHANGED.value)
        SocketService.socket.off(Events.PLAYER_LEFT_ROOM.value)
        SocketService.socket.off(Events.PLAYER_LEFT_ROOM.value)
        SocketService.socket.off(Events.UPDATE.value)
    }

    override fun update(dt: Float) {
        super.update(dt)
        ecs.update(dt)
        updatePlayersList()
        checkPowerups()
        checkWinner()
    }

    override fun render(sb: SpriteBatch) {
        // Enable transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        gameWidget.render()
        super.render(sb)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        gameWidget.width = width * (1.0f - SIDE_PANEL_SIZE)
        gameWidget.height = height.toFloat()
    }
}