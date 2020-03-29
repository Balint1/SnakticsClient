package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.snake.game.singletons.PlayerInfo
import com.snake.game.singletons.http.CreateRoomResponse
import com.snake.game.singletons.http.HttpService

class CreateRoom : MenuBaseState() {
    private var nameField: TextField? = null
    private var passwordField: TextField? = null
    private var nicknameField: TextField? = null

    init {
        setTitle("Create room")

        nameField = TextField("", skin, "big").apply {
            messageText = "room name"
            setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            addElement(this)
        }
        passwordField = TextField("", skin, "big").apply {
            messageText = "password (optional)"
            setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            addElement(this)
        }
        nicknameField = TextField("", skin, "big").apply {
            messageText = "Your nickname"
            setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            addElement(this)
        }

        val create = createTextButton("Create", (ELEMENT_WIDTH - SPACING) / 2f) {
            createRoom(nameField!!.text, passwordField!!.text)
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
    private fun createRoom(roomName: String, password: String) {
        Gdx.app.debug("UI", "CreateRoom::createRoom(%s)".format(roomName))
        showWaitDialog("Creating room...")
        HttpService.createRoom(roomName, password, 4, ::onRoomCreated)
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
            PlayerInfo.nickname = nicknameField!!.text
            PlayerInfo.password = passwordField!!.text
            StateManager.push(JoinRoomState(response.id, response.name, PlayerInfo.nickname!!, PlayerInfo.password!!))
        } else {
            showMessageDialog(response.message)
        }
    }

}