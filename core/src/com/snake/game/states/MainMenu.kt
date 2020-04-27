package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.snake.game.backend.SocketService

class MainMenu : MenuBaseState() {
    private val creteBtn = createTextButton("Create room") {
        StateManager.push(CreateRoom())
    }
    private val joinBtn = createTextButton("Join room") {
        StateManager.push(RoomList())
    }
    private val generalSettingsTexture = Texture("buttons/vol-settings.png")
    private val generalSettings = createImageButton(generalSettingsTexture, ELEMENT_HEIGHT) {
        StateManager.push(Settings())
    }

    private val exitTexture = Texture("buttons/exit.png")
    private val exitButton = createImageButton(exitTexture, ELEMENT_HEIGHT) {
        onBackPressed()
    }

    init {
        if (!SocketService.socket.connected()) {
            SocketService.tryConnect(::onTryingConnect)
            SocketService.addListeners(::onSocketConnect)
        } else {
            setTitle("Snaktics")
            addElement(creteBtn)
            addElement(joinBtn)
            addElements(exitButton, generalSettings, spacing = ELEMENT_WIDTH/2)
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
            addElement(creteBtn)
            addElement(joinBtn)
            addElements(exitButton, generalSettings, spacing = ELEMENT_WIDTH/2)
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
            showButtonDialog("Couldn't connect", "Retry") {
                SocketService.tryConnect(::onTryingConnect)
            }
        }
    }
}