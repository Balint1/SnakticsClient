package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class RoomList : MenuBaseState() {

    init {
        setTitle("Join room")

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