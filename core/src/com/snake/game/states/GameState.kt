package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.google.gson.Gson
import com.snake.game.backend.*
import com.snake.game.ecs.SnakeECSEngine
import org.json.JSONException
import org.json.JSONObject

class GameState(private val roomId: String, private val playerId: String) : MenuBaseState() {
    private val slider: Slider = Slider(-3f, 3f, 1f, false, skin)

    private val ecs = SnakeECSEngine

    init {
        cancelOldListeners()
        addListeners()
        slider.value = 0f
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                emitSliderValue(slider.value.toInt())
            }
        })
        addElement(slider)
        createTextButton("back") {
            SocketService.socket.emit(Events.LEAVE_TO_LOBBY.value)
        }.apply {
            addElement(this)
        }
    }

    override fun activated() {
        super.activated()

        ecs.createEntities()
    }

    private fun emitSliderValue(value: Int) {
        SocketService.socket.emit(Events.SLIDER_CHANGE.value, Data.SLIDER_CHANGE(value))
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE.value) { args ->
            onStateUpdate(args)
        }.on(Events.PLAYER_LEFT_GAME.value) {args ->
            Gdx.app.log("SocketIO", "PLAYER_LEFT_GAME")
            playerLeftGame(args)
        }.on(Events.LEAVE_TO_LOBBY_RESPONSE.value) {args ->
            Gdx.app.log("SocketIO", "LEAVE_TO_LOBBY_RESPONSE")
            leaveToLobby(args)
        }
    }

    private fun cancelNewListeners() {
        SocketService.socket.off(Events.PLAYER_LEFT_GAME.value)
        SocketService.socket.off(Events.LEAVE_TO_LOBBY_RESPONSE.value)
        SocketService.socket.off(Events.UPDATE.value)
    }
    private fun cancelOldListeners(){
        SocketService.socket.off(Events.OWNER_CHANGED.value)
        SocketService.socket.off(Events.PLAYER_LEFT_ROOM.value)
    }

    private fun onStateUpdate(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        try {
            val state = data.getJSONArray("state")
            Gdx.app.log("SocketIO", "state: $state")
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }

    override fun update(dt: Float) {
        super.update(dt)
        ecs.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        super.render(sb)
        ecs.render(sb)
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
        hideDialog()
        if (response.success) {
            cancelNewListeners()
            StateManager.pop()
        } else {
            showMessageDialog(response.message)
        }
    }

    private fun playerLeftGame(args: Array<Any>){
        val data: JSONObject = args[0] as JSONObject
        val response: PlayerLeftGame = Gson().fromJson(data.toString(), PlayerLeftGame::class.java)
        Gdx.app.debug("UI", "GameState::playerLeftGame(%s, %b)".format(response.id, response.success))
        if (response.success) {
            //TODO Stop rendering 'response.id' player.
        } else {
            showMessageDialog(response.message)
        }
    }
}