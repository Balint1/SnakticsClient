package com.snake.game.states

class MainMenu : MenuBaseState() {

    init {
        setTitle("Snaktics")

        createTextButton("Create room") {
            StateManager.push(CreateRoom())
        }.apply {
            addElement(this)
        }

        createTextButton("Join room") {
            StateManager.push(RoomList())
        }.apply {
            addElement(this)
        }

        createTextButton("Settings") {
            StateManager.push(Settings())
        }.apply {
            addElement(this)
        }
    }
}