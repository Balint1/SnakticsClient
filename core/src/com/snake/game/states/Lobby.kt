package com.snake.game.states

class Lobby : MenuBaseState() {

    init {
        setTitle("Lobby")

        createTextButton("Play") {
            StateManager.push(BaseGameState())
        }.apply {
            addElement(this)
        }

        createTextButton("Leave") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }
}