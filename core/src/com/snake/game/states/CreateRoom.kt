package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.TextField

class CreateRoom : MenuBaseState() {
    var nameField : TextField? = null

    init {
        setTitle("Create room")

        nameField = TextField("", skin, "big").apply {
            messageText = "room name";
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

    private fun createRoom(roomName: String) {
        Gdx.app.debug("UI", "CreateRoom::createRoom(%s)".format(roomName));
        // TODO: Create room
    }
}