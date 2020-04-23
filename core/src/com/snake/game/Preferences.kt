package com.snake.game

object Preferences {
    var volume: Float = 0.5f

    var customSpeed = 0.5f
    var snakeLength = "normal"
    var colorsDisabled = false

    fun apply(customSpeed: Float, snakeLength: String, colorsDisabled: Boolean) {
        this.customSpeed = customSpeed
        this.snakeLength = snakeLength
        this.colorsDisabled = colorsDisabled
    }

    fun discard() {
        customSpeed = 0.5f
        snakeLength = "normal"
        colorsDisabled = false
    }
}