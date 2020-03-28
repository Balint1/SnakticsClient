package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.snake.game.singletons.http.HttpService
import com.snake.game.singletons.http.StartGameResponse
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.Data
import com.snake.game.singletons.sockets.SocketService
import org.json.JSONException
import org.json.JSONObject

class Lobby(private val roomId: String, ownerId: String) : MenuBaseState() {

    private var players: MutableList<Player> = mutableListOf()
    private var nickname: String = "my_nickname"
    private val playerId: String = SocketService.socket.id()

    init {
        requestJoin()
        addListeners()

        setTitle("Lobby")

        if (ownerId == playerId) {
            createTextButton("Play") {
                showWaitDialog("Starting game...")
                HttpService.startGame(roomId, playerId, ::onStartGame)
            }.apply {
                addElement(this)
            }
        }

        createTextButton("Leave") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }

    private fun requestJoin() {
        SocketService.socket.emit(Events.JOIN_REQUEST, Data.JOIN_REQUEST(nickname, roomId))
    }

    private fun addListeners() {
        SocketService.socket
                .on(Events.JOIN_SUCCEEDED) { args ->
                    joinSucceeded(args)
                }.on(Events.JOIN_FAILED) { args ->
                    joinFailed(args)
                }.on(Events.NEW_PLAYER) { args ->
                    newPlayerJoined(args)
                }
    }

    private fun joinSucceeded(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        try {
            val id: String = data.getString("id")
            players.add(Player(nickname, id))

            val message: String = data.getString("message")
            Gdx.app.log("SocketIO", "$message,  socket id: $id")
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }

    private fun joinFailed(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        try {
            val message: String = data.getString("message")
            val id: String = data.getString("id")
            Gdx.app.log("SocketIO", "$message, socket id: $id")
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }

    }

    private fun newPlayerJoined(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        try {
            val id: String = data.getString("id")
            val nickname = data.getString("nickname")
            players.add(Player(nickname, id))
            Gdx.app.log("SocketIO", "New Player Connected: $id")
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }

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

data class Player(
        var nickname: String,
        var id: String
)