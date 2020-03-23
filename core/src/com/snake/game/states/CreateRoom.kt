package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField

class CreateRoom : MenuBaseState() {
    private var nameField: TextField? = null
    private var waitDialog: Dialog? = null

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

        waitDialog = (object : Dialog("", skin, "default") {
        }).apply {
            addElement(Label("Getting rooms...", skin, "big"), parent = contentTable)
            pad(SPACING / 5f)
            isMovable = false;
            isResizable = false;
            if (DEBUG_LAYOUT)
                contentTable.debug()
            show(super.stage)
        }

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

        waitDialog?.hide(null)
        if(success) {
            StateManager.push(Lobby())
        }
        else {
            (object : Dialog("", skin, "default") {
                override fun result(result: Any) {

                }
            }).apply {
                addElement(Label("Failed to create room", skin, "big"), parent = contentTable)
                button("Ok", true)
                pad(SPACING / 5f)
                isMovable = false;
                isResizable = false;
                if (DEBUG_LAYOUT)
                    contentTable.debug()
                show(super.stage)
            }
        }
    }
}