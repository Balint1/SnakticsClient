package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.snake.game.singletons.http.CreateRoomResponse
import com.snake.game.singletons.http.HttpService

class CreateRoom : MenuBaseState() {
    private var nameField: TextField? = null

    init {
        setTitle("Create room")

        nameField = TextField("", skin, "big").apply {
            messageText = "room name"
            setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            addElement(this)
        }

        val create = createTextButton("Create", (ELEMENT_WIDTH - SPACING) / 2f) {
            createRoom(nameField!!.text)
        }

        val back = createTextButton("Back", (ELEMENT_WIDTH - SPACING) / 2f) {
            StateManager.pop()
        }

        addElements(back, create, spacing = SPACING)
    }

    /**
     * Attempts to create a room
     *
     * @param roomName The name of the room
     */
    private fun createRoom(roomName: String) {
        Gdx.app.debug("UI", "CreateRoom::createRoom(%s)".format(roomName))
        showWaitDialog("Creating room...")
        // TODO: Create room async
        val response = HttpService.createRoom(roomName, 4)
        onRoomCreated(response) // Remove this
    }

    /**
     * Called when the room is created or failed to create the room
     *
     * @param response response fom create room http request
     */
    private fun onRoomCreated(response: CreateRoomResponse) {
        Gdx.app.debug("UI", "CreateRoom::onRoomCreated(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.push(Lobby())
        } else {
            showMessageDialog("Failed to create room: ${response.message}")
        }
    }
}