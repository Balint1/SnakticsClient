package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.ExtendViewport
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
import com.snake.game.ecs.component.TagComponent
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
) : BaseState() {

    // TODO get from backend
    private val FIELD_WIDTH: Float = 500f
    private val FIELD_HEIGHT: Float = 300f

    private val ecs = SnakeECSEngine
    private val swipeDetector = SwipeDetector
    private val gameWidget = GameWidget(ecs, FIELD_WIDTH, FIELD_HEIGHT)
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
        splitPane.setBounds(0f, 0f, MenuBaseState.VIRTUAL_WIDTH, MenuBaseState.VIRTUAL_HEIGHT)
        splitPane.splitAmount = 0.2f
        splitPane.minSplitAmount = 0.2f
        splitPane.maxSplitAmount = 0.2f

        updatePlayersList()

        infoPane = InfoPane(uiGroup, splitPane.width * splitPane.splitAmount, splitPane.height)
        itemPowerups = ItemPowerups(infoPane.width, infoPane.height)

        val backButton = createTextButton("lobby") {
            SocketService.socket.emit(Events.LEAVE_TO_LOBBY.value)
        }

        stage.addActor(splitPane)

        infoPane.addRow(playersList)
        infoPane.addRow(itemPowerups.getPowerupsControlPanel())
        infoPane.addBackButton(backButton)

        // we add swipe listener :
        swipeDetector.active = true
        itemPowerups.pickup(TagComponent.EntityTagType.Fireball)
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
            setSize(MenuBaseState.ELEMENT_WIDTH * splitPane.splitAmount * 0.4F, MenuBaseState.ELEMENT_HEIGHT / 2)
        }

        val table = Table()
        var widthTotal = 0f
        var heightTotal = 0f

        if (!alive) {
            val aliveIcon = Image(Texture("indicators/dead-red.png")).apply {
                width = MenuBaseState.ELEMENT_WIDTH * splitPane.splitAmount * 0.15F
                height = width
            }

            table.add(aliveIcon).width(aliveIcon.width).height(aliveIcon.height).padRight(aliveIcon.width / 2)
            widthTotal += (aliveIcon.width + aliveIcon.width / 2)
            heightTotal += heightTotal.coerceAtLeast(aliveIcon.height)
        }

        if (invisible) {
            val invisibleIcon = Image(Texture("powerup-sprites/powerup-invisible.png")).apply {
                width = MenuBaseState.ELEMENT_WIDTH * splitPane.splitAmount * 0.15F
                height = width
            }

            table.add(invisibleIcon).width(invisibleIcon.width).height(invisibleIcon.height).padRight(invisibleIcon.width / 2)
            widthTotal += (invisibleIcon.width + invisibleIcon.width / 2)
            heightTotal += heightTotal.coerceAtLeast(invisibleIcon.height)
        }

        table.add(nicknameLabel).width(nicknameLabel.width).height(nicknameLabel.height)
        widthTotal += nicknameLabel.width
        heightTotal += heightTotal.coerceAtLeast(nicknameLabel.height)
        table.setSize(MenuBaseState.ELEMENT_WIDTH * splitPane.splitAmount * 0.5F, MenuBaseState.ELEMENT_HEIGHT / 2)
        playersList.add(table).width(table.width).height(table.height).padTop(nicknameLabel.height / 3f).expandX()
        playersList.row()
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE.value) { args ->
            Gdx.app.log("SocketIO", "UPDATE " + args[0].toString())
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
        }.on(Events.PLAYER_DIED.value) { args ->
            Gdx.app.log("SocketIO", "PLAYER_DIED")
            val data: JSONObject = args[0] as JSONObject
            val response: PlayerEvent = Gson().fromJson(data.toString(), PlayerEvent::class.java)
            playerDied(response.id)
        }.on(Events.YOU_DIED.value) {
            Gdx.app.log("SocketIO", "YOU_DIED")
            Gdx.app.postRunnable {
                youDied()
            }
        }
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

    private fun onStateUpdate(args: Array<Any>) {
        val em = ecs.entityManager

        val data: JSONObject = args[0] as JSONObject
        try {
            val state = data.getJSONArray("state")

            // Update components
            for (i in 0 until state.length()) {
                val componentData = state.getJSONObject(i)
                val id: String = componentData.getString("entityId")
                val componentTypeName = componentData.getString("componentType")

                val componentType: ComponentType? = ComponentType.fromName(componentTypeName)
                        ?: continue // skip if component type doesn't exist

                // Create the entity if necessary
                if (!em.hasEntity(id))
                    Entity(id, em)

                val entity = em.getEntity(id)!!

                // Create the component if necessary
                if (!entity.hasComponent(componentType!!))
                    entity.addComponent(ComponentType.createComponent(componentType))

                val component = entity.getComponent(componentType)!!
                component.updateFromJSON(componentData)
            }
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }

    override fun update(dt: Float) {
        super.update(dt)
        ecs.update(dt)
        updatePlayersList()
    }

    /**
     * Called when the player success or fails to leave to the lobby
     *
     * @param args data from socket
     */
    private fun leaveToLobby(args: Array<Any>) {
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
        // TODO: remove the player from the player list for the side panel, and update the player list in the side panel
        // TODO Stop rendering 'response.id' player.
    }

    private fun playerDied(id: String) {

        Gdx.app.debug("UI", "GameState::playerLeftGame(%s)".format(id))
        // TODO: add some indicator that player is dead in side panel.
        // TODO Stop rendering 'response.id' player.
    }

    private fun youDied() {
        infoPane.showDeathMessage(skin)
    }

    private fun deleteEntities(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val message: DeleteEntities = Gson().fromJson(data.toString(), DeleteEntities::class.java)

        for (entityId in message.entityIds)
            ecs.removeEntity(entityId)
    }
}

class GameWidget(
        val ecs: SnakeECSEngine,
        private val fieldWidth: Float,
        private val fieldHeight: Float
) : Widget() {

    private val viewport = ExtendViewport(fieldWidth, fieldHeight, fieldWidth, fieldHeight)

    // The SpriteBatch used for rendering the game
    private val sb = SpriteBatch()

    private fun updateSize() {

        width = MenuBaseState.VIRTUAL_WIDTH * 0.8f
        height = MenuBaseState.VIRTUAL_HEIGHT

        viewport.update(width.toInt(), height.toInt(), true)
        viewport.setScreenBounds(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        /*var ratio: Float = fieldWidth / fieldHeight
        var h = height
        var gwidth = (h * ratio)
        viewport.update(gwidth.toInt(), h.toInt(), true)*/
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        // TODO update only when necessary
        updateSize()

        viewport.apply(true)
        sb.projectionMatrix = viewport.camera.combined
        ecs.render(sb, fieldWidth, fieldHeight)
    }
}
