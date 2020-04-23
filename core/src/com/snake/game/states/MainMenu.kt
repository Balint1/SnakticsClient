package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.snake.game.backend.SocketService

class MainMenu : MenuBaseState() {

    init {
        if (!SocketService.socket.connected()) {
            SocketService.tryConnect(::onTryingConnect)
            SocketService.addListeners(::onSocketConnect)
        } else {
            setTitle("Snaktics")
            createTextButton("Create room") {
                StateManager.push(CreateRoom())
            }.apply {
                addElement(this)
            }

            createTextButton("Join room") {
                StateManager.push(RoomList())
            }.apply {
                addElement(this)
            }

            createTextButton("Settings") {
                StateManager.push(Settings())
            }.apply {
                addElement(this)
            }
        }
    }

    /**
     * Called when game succeeds or fails to connect socket to the server
     *
     * @param success weather socket managed to connect
     */
    private fun onSocketConnect(success: Boolean, message: String) {
        Gdx.app.debug("UI", "ConnectSocket::onSocketConnect(%b)".format(success))
        hideDialog()
        if (success) {
            setTitle("Snaktics")
            createTextButton("Create room") {
                StateManager.push(CreateRoom())
            }.apply {
                addElement(this)
            }

            createTextButton("Join room") {
                StateManager.push(RoomList())
            }.apply {
                addElement(this)
            }

            createTextButton("Settings") {
                StateManager.push(Settings())
            }.apply {
                addElement(this)
            }
        } else {
            showButtonDialog(message, "Ok")
        }
    }

    /**
     * Called connection countdown
     *
     * @param trying weather server is still trying to connect
     * @param seconds how much time is left
     */
    private fun onTryingConnect(trying: Boolean, seconds: Int) {
        hideDialog()
        if (trying) {
            showWaitDialog("Connecting...$seconds")
        } else {
            showButtonDialog("Couldn't connect","Retry") {
                SocketService.tryConnect(::onTryingConnect)
            }
        }
    }
}