package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.snake.game.singletons.sockets.SocketService

class MainMenu : MenuBaseState() {

    private var retryCreated: Boolean = false

    init {
        setTitle("Snaktics")
        SocketService.addConnectListener(::onSocketConnect)
        SocketService.tryConnect(::onTryingConnect)
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
            if (retryCreated) {
                retryCreated = false
                removeButton("Retry")
            }
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
            showMessageDialog(message)
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
            showWaitDialog("Connecting...${seconds}")
        } else {
            if (!retryCreated) {
                retryCreated = true
                createTextButton("Retry") {
                    SocketService.tryConnect(::onTryingConnect)
                }.apply {
                    addElement(this)
                }
            }
        }

    }

}