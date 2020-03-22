package com.snake.game.states

class Lobby : MenuBaseState() {

    init {
        setTitle("Lobby")

        createTextButton("Leave") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }
}