package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class CreateRoom : MenuBaseState() {

    init {
        setTitle("Create room")

        createTextButton("Back", skin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    StateManager.pop()
                }
            })
            addMenuElement(this)
        }
    }
}