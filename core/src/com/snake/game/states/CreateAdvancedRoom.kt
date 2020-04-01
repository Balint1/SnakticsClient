package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.TextField

class CreateAdvancedRoom : CreateRoom() {

    private var capacityField: SelectBox<Int> = SelectBox<Int>(skin, "big")
            .apply {
                setItems(4, 3, 2, 1)
                setSize((ELEMENT_WIDTH - SPACING) * 1 / 4, ELEMENT_HEIGHT)
            }
    private var passwordField = TextField("", skin, "big")
            .apply {
                messageText = "password"
                setSize((ELEMENT_WIDTH - SPACING) * 3 / 4, ELEMENT_HEIGHT)
            }

    init {

        rootTable.clear()
        setTitle("Create Advanced room")

        addElement(nameField)
        addElement(nicknameField)
        addElements(passwordField, capacityField, spacing = SPACING)

        val create = createTextButton("Create", (ELEMENT_WIDTH - SPACING) / 2f) {
            password = passwordField.text
            createRoom(nameField.text, password, capacityField.selected)
        }

        val back = createTextButton("Back", (ELEMENT_WIDTH - SPACING) / 2f) {
            StateManager.pop()
        }

        addElements(back, create, spacing = SPACING)
    }
}