package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.snake.game.http.GetRoomsResponse
import com.snake.game.http.HttpService
import com.snake.game.http.Room

class RoomList : MenuBaseState() {
    private val roomList = Table()

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
        showMessageDialog("Getting rooms...", "Cancel") {
            HttpService.cancelGetRooms()
            StateManager.pop()
        }

        HttpService.getRooms(::onReceivedRoomList)
    }

    /**
     * Called when the list of rooms is received from the server
     *
     * @param response http response with the list of rooms
     */
    private fun onReceivedRoomList(response: GetRoomsResponse) {
        Gdx.app.debug("UI", "RoomList::onReceivedRoomList(%d)".format(response.rooms.size))
        hideDialog()
        roomList.clear()
        for (room in response.rooms) {
            val nameLabel = Label(room.name, skin, "title").apply {
                setSize(ELEMENT_WIDTH * 3 / 7, ELEMENT_HEIGHT / 2)
            }
            val capacityLabel = Label("(${room.players}/${room.capacity})", skin, "title").apply {
                setSize(ELEMENT_WIDTH * 1 / 7, ELEMENT_HEIGHT / 2)
            }
            val inProgressLabel = Label(if(room.inProgress) "Started" else "", skin, "title").apply {
                setSize(ELEMENT_WIDTH * 2 / 7, ELEMENT_HEIGHT / 2)
            }
            val joinButton = createTextButton("Join", isDisabled = room.inProgress) {
                joinRoom(room)
            }.apply {
                setSize(ELEMENT_WIDTH * 1 / 7, ELEMENT_HEIGHT / 2)
            }
            addElements(capacityLabel, nameLabel, inProgressLabel, joinButton, parent = roomList, padTop = nameLabel.height / 2f)
        }
    }

    /**
     * Attempts to join a room
     *
     * @param room The room to join
     * @param nickname The potential nickname in game
     * @param password The password to the room
     */
    private fun joinRoom(room: Room, nickname: String, password: String) {
        Gdx.app.debug("UI", "RoomList::joinRoom(%s, %s)".format(room.name, password))
        StateManager.push(JoinRoomState(room.id, room.name, nickname, password))
    }


    /**
     * Attempts to join a room
     * Displays a password prompt if the room is password protected
     *
     * @param room The room to join
     */
    private fun joinRoom(room: Room) {
        if (room.hasPassword) {
            val passwordField = TextField("", skin, "big").apply {
                messageText = "password"
                width = ELEMENT_WIDTH / 2f
            }
            val nicknameField = TextField("", skin, "big").apply {
                messageText = "nickname"
                width = ELEMENT_WIDTH / 2f
            }
            val label = Label("Enter password and nickname", skin, "big").apply {
                width = ELEMENT_WIDTH / 2f
            }
            (object : Dialog("", skin, "default") {
                override fun result(result: Any) {
                    if (result is Boolean && result) {
                        joinRoom(room, nicknameField.text, passwordField.text)
                    }
                }
            }).apply {
                button("Cancel", false)
                button("Join", true).buttonTable.cells[0].padRight(SPACING / 2f)
                addElement(label, parent = contentTable)
                addElement(nicknameField, parent = contentTable, padTop = 10f)
                addElement(passwordField, parent = contentTable, padTop = 10f)
                pad(SPACING / 5f)
                isMovable = false
                isResizable = false
                if (DEBUG_LAYOUT)
                    contentTable.debug()
                show(super.stage)
            }
        } else {
            val nicknameField = TextField("", skin, "big").apply {
                messageText = "nickname"
                width = ELEMENT_WIDTH / 2f
            }
            val label = Label("Enter nickname", skin, "big").apply {
                width = ELEMENT_WIDTH / 2f
            }
            (object : Dialog("", skin, "default") {
                override fun result(result: Any) {
                    if (result is Boolean && result) {
                        joinRoom(room, nicknameField.text, "")
                    }
                }
            }).apply {
                button("Cancel", false)
                button("Join", true).buttonTable.cells[0].padRight(SPACING / 2f)
                addElement(label, parent = contentTable)
                addElement(nicknameField, parent = contentTable, padTop = 10f)
                pad(SPACING / 5f)
                isMovable = false
                isResizable = false
                if (DEBUG_LAYOUT)
                    contentTable.debug()
                show(super.stage)
            }
        }
    }
}