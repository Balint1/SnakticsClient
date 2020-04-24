package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.google.gson.Gson
import com.snake.game.backend.*
import com.snake.game.controls.SwipeDetector
import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.componentTypeFromInternalName
import com.snake.game.ecs.component.createComponent
import com.snake.game.ecs.entity.Entity
import org.json.JSONException
import org.json.JSONObject


class GameState(private val roomId: String, private val playerId: String) : BaseState() {
    private val swipeDetector = SwipeDetector

    // TODO get from backend
    private val FIELD_WIDTH: Float = 500f
    private val FIELD_HEIGHT: Float = 300f

    private val ecs = SnakeECSEngine
    private val gameWidget = GameWidget(ecs, FIELD_WIDTH, FIELD_HEIGHT)

    init {
        cancelOldListeners()
        addListeners()

        val uiGroup = VerticalGroup()

        val splitPane = SplitPane(uiGroup, gameWidget, false, skin)
        splitPane.setBounds(0f, 0f, MenuBaseState.VIRTUAL_WIDTH, MenuBaseState.VIRTUAL_HEIGHT)
        splitPane.splitAmount = 0.2f
        splitPane.minSplitAmount = 0.2f
        splitPane.maxSplitAmount = 0.2f

        stage.addActor(splitPane)
        // element can be added to  the side panel with :
        // uiGroup.addActor(joystickInput.touchpad)

        createTextButton("back") {
            SocketService.socket.emit(Events.LEAVE_TO_LOBBY.value)
        }.apply {
            uiGroup.addActor(this)
        }

        // we add swipe listener :
        swipeDetector.active = true
    }

    override fun activated() {
        super.activated()

        ecs.createEntities()
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE.value) { args ->
            onStateUpdate(args)
        }.on(Events.DELETE_ENTITIES.value) { args ->
            Gdx.app.log("SocketIO", "DELETE_ENTITY")
            deleteEntities(args)
        }.on(Events.PLAYER_LEFT_GAME.value) { args ->
            Gdx.app.log("SocketIO", "PLAYER_LEFT_GAME")
            playerLeftGame(args)
        }.on(Events.LEAVE_TO_LOBBY_RESPONSE.value) { args ->
            Gdx.app.log("SocketIO", "LEAVE_TO_LOBBY_RESPONSE")
            leaveToLobby(args)
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

                val componentType: ComponentType? = componentTypeFromInternalName(componentTypeName)
                        ?: continue // skip if component type doesn't exist

                // Create the entity if necessary
                if (!em.hasEntity(id))
                    Entity(id, em)
                val entity = em.getEntity(id)!!

                // Create the component if necessary
                if (!entity.hasComponent(componentType!!))
                    entity.addComponent(createComponent(componentType))

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

    private fun playerLeftGame(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val response: PlayerLeftGame = Gson().fromJson(data.toString(), PlayerLeftGame::class.java)
        Gdx.app.debug("UI", "GameState::playerLeftGame(%s, %b)".format(response.id, response.success))
        if (response.success) {
            // TODO Stop rendering 'response.id' player.
        } else {
            Gdx.app.log("UI", "::playerLeftGame Error: ${response.message}")
        }
    }

    private fun deleteEntities(args: Array<Any>) {
        val data: DeleteEntities = Gson().fromJson((args[0] as JSONObject).toString(), DeleteEntities::class.java)

        for(entityId in data.entityIds)
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
        sb.begin()
        ecs.render(sb, fieldWidth, fieldHeight)
        sb.end()
    }
}