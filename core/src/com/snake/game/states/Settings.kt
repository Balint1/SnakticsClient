package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.snake.game.Preferences


class Settings : MenuBaseState() {

    init {
        setTitle("Settings")

        Slider(0f, 1f, 0.1f, false, skin).apply {
            setSize(BUTTON_WIDTH, BUTTON_HEIGHT)
            value = Preferences.volume
            addListener {
                Preferences.volume = value
                false
            }
            addMenuElement(this)
        }

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