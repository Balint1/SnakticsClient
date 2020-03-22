package com.snake.game.states

class AdvancedSettings : MenuBaseState() {

    init {
        setTitle("Advanced settings")

        createTextButton("Back") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }
}