package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener

class MainMenu : MenuBaseState() {

    init {
        setTitle("Snaktics")

        createTextButton("Join room", skin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    StateManager.push(RoomList())
                }
            })
            addMenuElement(this)
        }

        createTextButton("Create room", skin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    StateManager.push(CreateRoom())
                }
            })
            addMenuElement(this)
        }

        createTextButton("Settings", skin).apply {
            addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    StateManager.push(Settings())
                }
            })
            addMenuElement(this)
        }
    }
}