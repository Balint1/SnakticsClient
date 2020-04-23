package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import com.snake.game.backend.Events
import com.snake.game.backend.JoinResponse
import com.snake.game.backend.SocketService
import org.json.JSONObject

class JoinRoomState(
    private val roomId: String,
    private val roomName: String,
    private val nickname: String,
    private val password: String
) : MenuBaseState() {

    init {
        addListeners()
        joinRoom()
    }

    /**
     * Attempts to join a room
     */
    private fun joinRoom() {
        Gdx.app.debug("UI", "JoinRoomState::joinRoom(%s, %s)".format(roomName, password))
        SocketService.joinRoom(nickname, roomId, password)
    }

    /**
     * Start listening to responses on room join events
     */
    private fun addListeners() {
        SocketService.socket.on(Events.JOIN_RESPONSE.value) { args ->
            Gdx.app.postRunnable {
                onRoomJoined(args)
            }
        }
    }

    /**
     * Called when the player joins or fails to join the room
     *
     * @param args Socket response on join room request
     */
    private fun onRoomJoined(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val response: JoinResponse = Gson().fromJson(data.toString(), JoinResponse::class.java)
        Gdx.app.debug("UI", "JoinRoomState::onRoomJoined(${response.roomId}, %b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.push(Lobby(response.roomId, response.players))
        } else {
            showButtonDialog(response.message, "Ok") {
                StateManager.pop()
            }
        }
    }
}