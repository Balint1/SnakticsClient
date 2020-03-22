package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.*

class RoomList : MenuBaseState() {
    init {
        setTitle("Join room")

        val roomList = Table()
        for (room in getRooms()) {
            val nameLabel = Label(room.name, skin, "title").apply {
                setSize(ELEMENT_WIDTH * 2 / 3, ELEMENT_HEIGHT / 2)
            }
            val joinButton = createTextButton("Join"){
                joinRoom(room)
            }.apply {
                setSize(ELEMENT_WIDTH * 1 / 3, ELEMENT_HEIGHT / 2)
            }
            addElements(nameLabel, joinButton, parent = roomList, padTop = nameLabel.height / 2f)
        }

        val scrollPane = ScrollPane(roomList)
        scrollPane.setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT * 4)
        scrollPane.setScrollBarPositions(true, true)
        addElement(scrollPane)

        createTextButton("Back") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }

    private fun joinRoom(room: MockRoom) {
        if(room.hasPassword) {
            val passwordField = TextField("", skin).apply {
                messageText = "password";
            }

            (object : Dialog("", skin) {
                override fun result(result: Any) {
                    if (result is Boolean && result) {
                        joinRoom(room, passwordField.text)
                    }
                }
            }).apply {
                text("Enter password")
                button("Cancel", false)
                button("Join", true)
                contentTable.add(passwordField)
                if(DEBUG_LAYOUT)
                    contentTable.debug()
                show(super.stage)
            }
        }
        else {
            joinRoom(room, "")
        }
    }

    private fun joinRoom(room: MockRoom, password: String) {
        Gdx.app.debug("UI", "RoomList::joinRoom(%s, %s)".format(room.name, password));
        // TODO: Join room
    }

    private fun getRooms() : Array<MockRoom> {
        Gdx.app.debug("UI", "RoomList::getRooms");
        // TODO: Get rooms
        return MockRoom.rooms
    }
}

class MockRoom(var name: String, var hasPassword: Boolean = false) {
    companion object {
        val rooms: Array<MockRoom> = arrayOf(
                MockRoom("A room"),
                MockRoom("Another room"),
                MockRoom("Password room", true),
                MockRoom("Room name"),
                MockRoom("A longer room name"),
                MockRoom("Room"),
                MockRoom("Room"),
                MockRoom("Room"),
                MockRoom("Room")
        )
    }
}