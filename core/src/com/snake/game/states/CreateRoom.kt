package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.TextField

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
        // backend.createRoom(roomName, ::onRoomCreated)
        onRoomCreated(false) // Remove this
    }

    /**
     * Called when the room is created or failed to create the room
     *
     * @param success True if the room was created
     */
    private fun onRoomCreated(success: Boolean) {
        Gdx.app.debug("UI", "CreateRoom::onRoomCreated(%b)".format(success))
        hideDialog()
        if (success) {
            StateManager.push(Lobby())
        } else {
            showMessageDialog("Failed to create room")
        }
    }
}