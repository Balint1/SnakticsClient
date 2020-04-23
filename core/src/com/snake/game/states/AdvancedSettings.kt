package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.snake.game.Preferences

class AdvancedSettings : MenuBaseState() {
    private val maxSpeed = 1f
    private val minSpeed = 0f
    private var customSpeed = 0f
    private var snakeLength = ""
    private var colorsDisabled = false
    private var prevCustomSpeed = 0f
    private var prevSnakeLength = ""
    private var prevColorsDisabled = false
    private var snakeLengthSelector: SelectBox<String>? = null
    private var customSpeedSelector: Slider? = null
    private var colorsDisabledSelector: CheckBox? = null

    private val applyButton = createTextButton("Apply") {
        chooseAction()
    }.apply {
        setSize((ELEMENT_WIDTH - SPACING) * 1 / 2f, ELEMENT_HEIGHT)
    }

    init {
        setTitle("Advanced settings")

        addSnakeSpeedSelector()
        addSnakeLengthSelector()
        addColorCheckBox()

        val backButton = createTextButton("Back") {
            StateManager.pop()
        }.apply {
            setSize((ELEMENT_WIDTH - SPACING) * 1 / 2f, ELEMENT_HEIGHT)
        }

        addElements(backButton, applyButton, spacing = SPACING)
    }

    private fun addSnakeSpeedSelector() {
        val label = Label("Base Speed", skin, "title").apply {
            setSize((ELEMENT_WIDTH - SPACING) * 2 / 5f, ELEMENT_HEIGHT)
        }

        customSpeedSelector = Slider(minSpeed, maxSpeed, 0.1f, false, skin).apply {
            setSize((ELEMENT_WIDTH - SPACING) * 3 / 5f, ELEMENT_HEIGHT)
            value = Preferences.customSpeed
            addListener {
                checkChanges()
                customSpeed = value
                false
            }
        }

        addElements(label, customSpeedSelector!!, spacing = SPACING)
    }

    private fun addSnakeLengthSelector() {
        val label = Label("Snake Length", skin, "title").apply {
            setSize((ELEMENT_WIDTH - SPACING) * 2 / 5f, ELEMENT_HEIGHT)
        }

        snakeLengthSelector = SelectBox<String>(skin, "big")
                .apply {
                    setItems("small", "normal", "long", "super long")
                    setSize((ELEMENT_WIDTH - SPACING) * 3 / 5f, ELEMENT_HEIGHT)
                    selected = Preferences.snakeLength
                    addListener {
                        checkChanges()
                        snakeLength = selected
                        false
                    }
                }

        addElements(label, snakeLengthSelector!!, spacing = SPACING)
    }

    private fun addColorCheckBox() {
        val label = Label("Disable Colors", skin, "title").apply {
            setSize((ELEMENT_WIDTH - SPACING) * 2 / 5f, ELEMENT_HEIGHT)
        }

        colorsDisabledSelector = CheckBox("", skin).apply {
            setSize((ELEMENT_WIDTH - SPACING) * 3 / 5f, ELEMENT_HEIGHT)
            isChecked = Preferences.colorsDisabled
            addListener {
                checkChanges()
                colorsDisabled = isChecked
                false
            }
        }
        addElements(label, colorsDisabledSelector!!, spacing = SPACING)
    }

    private fun chooseAction() {
        if (applyButton.text.toString() == "Apply") {
            Preferences.apply(customSpeed, snakeLength, colorsDisabled)
            applyButton.setText("Discard")
        } else {
            Preferences.discard()
            customSpeedSelector!!.value = 0.5f
            snakeLengthSelector!!.selected = "normal"
            colorsDisabledSelector!!.isChecked = false
            applyButton.setText("Apply")
        }
    }

    private fun checkChanges() {
        var changeDetected = false
        when {
            prevColorsDisabled != colorsDisabled -> {
                changeDetected = true
                prevColorsDisabled = colorsDisabled
            }
            prevCustomSpeed != customSpeed -> {
                changeDetected = true
                prevCustomSpeed = customSpeed
            }
            prevSnakeLength != snakeLength -> {
                changeDetected = true
                prevSnakeLength = snakeLength
            }
        }
        if (changeDetected && applyButton.text.toString() == "Discard") {
            applyButton.setText("Apply")
        }
    }
}