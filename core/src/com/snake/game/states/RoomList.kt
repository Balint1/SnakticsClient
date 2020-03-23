package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField

class RoomList : MenuBaseState() {
    private val roomList = Table()
    private var waitDialog: Dialog? = null

    init {
        setTitle("Join room")

        val scrollPane = ScrollPane(roomList)
        scrollPane.setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT * 4)
        scrollPane.setScrollBarPositions(true, true)
        addElement(scrollPane)

        createTextButton("Back") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }

        updateRooms()
    }

    /**
     * Updates the room list
     *
     */
    private fun updateRooms() {
        Gdx.app.debug("UI", "RoomList::updateRooms")

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

        // TODO: Get rooms async
        // backend.getRooms(::onReceivedRoomList)
        onReceivedRoomList(MockRoom.rooms) // Remove this
    }

    /**
     * Called when the list of rooms is received from the server
     *
     * @param rooms The list of rooms
     */
    private fun onReceivedRoomList(rooms: Array<MockRoom>) {
        Gdx.app.debug("UI", "RoomList::onReceivedRoomList(%d)".format(rooms.size))

        waitDialog?.hide(null)
        roomList.clear()
        for (room in rooms) {
            val nameLabel = Label(room.name, skin, "title").apply {
                setSize(ELEMENT_WIDTH * 2 / 3, ELEMENT_HEIGHT / 2)
            }
            val joinButton = createTextButton("Join") {
                joinRoom(room)
            }.apply {
                setSize(ELEMENT_WIDTH * 1 / 3, ELEMENT_HEIGHT / 2)
            }
            addElements(nameLabel, joinButton, parent = roomList, padTop = nameLabel.height / 2f)
        }
    }

    /**
     * Attempts to join a room
     *
     * @param room The room to join
     * @param password The password to the room
     */
    private fun joinRoom(room: MockRoom, password: String) {
        Gdx.app.debug("UI", "RoomList::joinRoom(%s, %s)".format(room.name, password))

        waitDialog = (object : Dialog("", skin, "default") {
        }).apply {
            addElement(Label("Joining room...", skin, "big").apply {
                width = ELEMENT_WIDTH / 2f
            }, parent = contentTable)
            pad(SPACING / 5f)
            isMovable = false;
            isResizable = false;
            if (DEBUG_LAYOUT)
                contentTable.debug()
            show(super.stage)
        }

        // TODO: Join room async
        // backend.joinRoom(::onRoomJoined)
        onRoomJoined(false) // Remove this
    }

    /**
     * Called when the room is joined or failed to join the room
     *
     * @param success True is the room was successfully joined
     */
    private fun onRoomJoined(success: Boolean) {
        Gdx.app.debug("UI", "RoomList::onRoomJoined(%b)".format(success))

        waitDialog?.hide(null)
        if(success) {
            StateManager.push(Lobby())
        }
        else {
            (object : Dialog("", skin, "default") {
                override fun result(result: Any) {

                }
            }).apply {
                addElement(Label("Failed to join room", skin, "big"), parent = contentTable)
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

    /**
     * Attempts to join a room
     * Displays a password prompt if the room is password protected
     *
     * @param room The room to join
     */
    private fun joinRoom(room: MockRoom) {
        if (room.hasPassword) {
            val passwordField = TextField("", skin, "big").apply {
                messageText = "password"
                width = ELEMENT_WIDTH / 2f
            }
            val label = Label("Enter password", skin, "big").apply {
                width = ELEMENT_WIDTH / 2f
            }
            (object : Dialog("", skin, "default") {
                override fun result(result: Any) {
                    if (result is Boolean && result) {
                        joinRoom(room, passwordField.text)
                    }
                }
            }).apply {
                button("Cancel", false)
                button("Join", true).buttonTable.cells[0].padRight(SPACING / 2f)
                addElement(label, parent = contentTable)
                addElement(passwordField, parent = contentTable, padTop = 10f)
                pad(SPACING / 5f)
                isMovable = false;
                isResizable = false;
                if (DEBUG_LAYOUT)
                    contentTable.debug()
                show(super.stage)
            }
        } else {
            joinRoom(room, "")
        }
    }
}

class MockRoom(var name: String, var hasPassword: Boolean = false) {
    companion object {
        val rooms: Array<MockRoom> = arrayOf(
                MockRoom("A room"),
                MockRoom("Another room"),
                MockRoom("Password room", true),
                MockRoom("Password room 2", true),
                MockRoom("A longer room name"),
                MockRoom("Room"),
                MockRoom("Room"),
                MockRoom("Room"),
                MockRoom("Room")
        )
    }
}