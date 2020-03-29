package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.snake.game.singletons.http.HttpService
import com.snake.game.singletons.http.StartGameResponse
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.SocketService
import org.json.JSONException
import org.json.JSONObject

class Lobby(
        private val roomId: String,
        private val nickname: String,
        isOwner: Boolean) : MenuBaseState() {

    private val playerId: String = SocketService.socket.id()

    init {
        setTitle("Lobby")

        if (isOwner) {
            createTextButton("Play") {
                showWaitDialog("Starting game...")
                HttpService.startGame(roomId, playerId, ::onStartGame)
            }.apply {
                addElement(this)
            }
        }

        createTextButton("Leave") {
            StateManager.set(MainMenu())
        }.apply {
            addElement(this)
        }
    }

//    private fun addListeners() {
//        if (!SocketService.listeners.getValue(Events.NEW_PLAYER)) {
//            SocketService.listeners[Events.NEW_PLAYER] = true
//            SocketService.socket.on(Events.NEW_PLAYER) { args ->
//                newPlayerJoined(args)
//            }
//        }
//    }
//
//
//    private fun newPlayerJoined(args: Array<Any>) {
//        val data: JSONObject = args[0] as JSONObject
//        try {
//            val id: String = data.getString("id")
//            val nickname = data.getString("nickname")
//            Gdx.app.log("SocketIO", "New Player Connected: $id")
//        } catch (e: JSONException) {
//            Gdx.app.log("SocketIO", "Error getting attributes: $e")
//        }
//    }

    /**
     * Called when the player success or fails to to start the game
     *
     * @param response response fom create room http request
     */
    private fun onStartGame(response: StartGameResponse) {
        Gdx.app.debug("UI", "StartGame::onStartGame(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.push(GameState())
        } else {
            showMessageDialog(response.message)
        }
    }
}