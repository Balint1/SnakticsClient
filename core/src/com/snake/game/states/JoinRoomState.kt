package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import com.snake.game.singletons.PlayerInfo
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.JoinResponse
import com.snake.game.singletons.sockets.SocketService
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
     *
     */
    private fun joinRoom() {
        Gdx.app.debug("UI", "JoinRoomState::joinRoom(%s, %s)".format(roomName, password))
        SocketService.joinRoom(nickname, roomId, password)
    }

    private fun addListeners() {
//        if (!SocketService.listeners.getValue(Events.JOIN_RESPONSE)) {
//            SocketService.listeners[Events.JOIN_RESPONSE] = true
            SocketService.socket.on(Events.JOIN_RESPONSE) { args ->
                Gdx.app.postRunnable {
                    onRoomJoined(args, PlayerInfo.nickname!!)
                }
            }
//        }
    }

    /**
     * Called when the room is joined or failed to join the room
     *
     * @param args Socket response on join room request
     */
    private fun onRoomJoined(args: Array<Any>, nickname: String) {
        val data: JSONObject = args[0] as JSONObject
        val response: JoinResponse = Gson().fromJson(data.toString(), JoinResponse::class.java)
        Gdx.app.debug("UI", "JoinRoomState::onRoomJoined(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.push(Lobby(response.roomId, nickname, response.isOwner))
        } else {
            showMessageDialog(response.message, onResult = {
                StateManager.pop()
            })
        }
    }
}