package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.snake.game.Preferences
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.snake.game.backend.CreateRoomResponse
import com.snake.game.backend.HttpService

open class CreateRoom : MenuBaseState() {

    protected var nameField: TextField = TextField("", skin, "big")
            .apply {
                messageText = "room name"
                setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            }

    protected var nicknameField: TextField = TextField("", skin, "big")
            .apply {
                messageText = "Your nickname"
                setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            }

    protected var password = ""

    init {
        Preferences.discard()
        rootTable.clear()
        setTitle("Create room")

        addElement(nameField)
        addElement(nicknameField)

        val create = createTextButton("Create", (ELEMENT_WIDTH - SPACING) / 3f) {

            createRoom(nameField.text, password, 4)
        }

        val back = createTextButton("Back", (ELEMENT_WIDTH - SPACING) / 3f) {
            StateManager.pop()
        }

        val advancedRoom = createTextButton("advanced", (ELEMENT_WIDTH - SPACING) / 3f) {
            StateManager.push(CreateAdvancedRoom())
        }

        addElements(back, advancedRoom, create, spacing = SPACING)
    }

    /**
     * Attempts to create a room
     *
     * @param roomName The name of the room
     */
    protected fun createRoom(roomName: String, password: String, capacity: Int) {
        Gdx.app.debug("UI", "CreateRoom::createRoom(%s)".format(roomName))
        if (validateFields(roomName, nicknameField.text, password)) {
            showWaitDialog("Creating room...")
            HttpService.createRoom(roomName, password, capacity, Preferences.getSettings(), ::onRoomCreated)
        }
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
            StateManager.push(JoinRoomState(response.id, response.name, nicknameField.text, password))
        } else {
            showButtonDialog(response.message, "Ok")
        }
    }

    private fun validateFields(roomName: String, nickname: String, password: String): Boolean {
        if (roomName == "") {
            showButtonDialog("Please enter room name")
            return false
        } else if (nicknameField.text == "") {
            showButtonDialog("Please enter nickname")
            return false
        } else if (roomName.length > 15) {
            showButtonDialog("Maximum allowed room name length is 15 characters")
            return false
        } else if (nickname.length > 15) {
            showButtonDialog("Maximum allowed room name length is 15 characters")
            return false
        } else if (isAlphaNumeric(roomName) && isAlphaNumeric(password) && isAlphaNumeric(nicknameField.text)) {
            return true
        } else {
            showButtonDialog("All input fields must be alphanumeric")
            return false
        }
    }
}