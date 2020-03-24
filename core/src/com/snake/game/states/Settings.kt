package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.snake.game.Preferences

class Settings : MenuBaseState() {

    init {
        setTitle("Settings")

        val volumeLabel = Label("Volume", skin, "title").apply {
            setSize(ELEMENT_WIDTH * 1 / 3f, ELEMENT_HEIGHT)
        }

        val volumeSlider = Slider(0f, 1f, 0.1f, false, skin).apply {
            setSize(ELEMENT_WIDTH * 2 / 3f, ELEMENT_HEIGHT)
            value = Preferences.volume
            addListener {
                Preferences.volume = value
                false
            }
        }
        addElements(volumeLabel, volumeSlider)

        createTextButton("Back") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }
}