package com.snake.game.states

import com.badlogic.gdx.graphics.Texture
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

        val create = createTextButton("Create", (ELEMENT_WIDTH - SPACING) / 3f) {
            password = passwordField.text
            createRoom(nameField.text, password, capacityField.selected)
        }

        val back = createTextButton("Back", (ELEMENT_WIDTH - SPACING) / 3f) {
            StateManager.pop()
        }

        val settingsTexture = Texture("buttons/settings.png")
        val advancedSettings = creteImageButton(settingsTexture, ELEMENT_HEIGHT * 2 / 3, ELEMENT_HEIGHT * 2 / 3) {
            StateManager.push(AdvancedSettings())
        }

        addElements(back, create, advancedSettings, spacing = SPACING)
    }
}