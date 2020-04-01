package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import com.snake.game.http.HttpService
import com.snake.game.http.SimpleResponse
import com.snake.game.sockets.Events
import com.snake.game.sockets.SocketService
import org.json.JSONObject

class Lobby(private val roomId: String) : MenuBaseState() {

    private val playerId: String = SocketService.socket.id()

    init {
        addListeners()
        setTitle("Lobby")
        createTextButton("Play") {
            showWaitDialog("Starting game...")
            HttpService.startGame(roomId, playerId, ::onStartGame)
        }.apply {
            addElement(this)
        }

        createTextButton("Leave") {
            HttpService.leaveRoom(roomId, playerId, ::onLeaveRoom)
        }.apply {
            addElement(this)
        }
    }


    /**
     * Called when the player success or fails to to start the game
     *
     * @param response response fom create room http request
     */
    private fun onStartGame(response: SimpleResponse) {
        Gdx.app.debug("UI", "Lobby::onStartGame(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.push(GameState(roomId, playerId))
        } else {
            showMessageDialog(response.message)
        }
    }

    /**
     * Called when the player success or fails to to leave the room
     *
     * @param response response fom create room http request
     */
    private fun onLeaveRoom(response: SimpleResponse) {
        Gdx.app.debug("UI", "Lobby::onStartGame(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.set(MainMenu())
        } else {
            showMessageDialog(response.message)
        }
    }

    /**
     * Start listening to responses on owner change events
     */
    private fun addListeners() {
        SocketService.socket.on(Events.OWNER_CHANGED.value) { args ->
            Gdx.app.postRunnable {
                assignOwner(args)
            }
        }.on(Events.LEAVE_RESPONSE.value) { args ->
            Gdx.app.postRunnable {
                leaveSocketRoom(args)
            }
        }
    }

    private fun assignOwner(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val response: SimpleResponse = Gson().fromJson(data.toString(), SimpleResponse::class.java)
        Gdx.app.debug("UI", "Lobby::assignOwner(%b)".format(response.success))
        if (response.success) {
            showMessageDialog("Now you are the room owner ")
        }

    }

    private fun leaveSocketRoom(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val response: SimpleResponse = Gson().fromJson(data.toString(), SimpleResponse::class.java)
        if (!response.success) {
            Gdx.app.debug("UI", "Lobby::leaveSocketRoom(%b)".format(response.success))
        }
    }
}