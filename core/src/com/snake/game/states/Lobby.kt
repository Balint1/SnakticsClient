package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.snake.game.http.HttpService
import com.snake.game.http.SimpleResponse
import com.snake.game.sockets.SocketService

class Lobby(private val roomId: String) : MenuBaseState() {

    private val playerId: String = SocketService.socket.id()

    init {
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
            StateManager.push(GameState())
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
}