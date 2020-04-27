package com.snake.game

object Preferences {
    var volume: Float = 0.5f
    var FIELD_WIDTH: Float = 500f
    var FIELD_HEIGHT: Float = 300f
    var speed = 0.5f
    var snakeLength = "normal"
    var colorsDisabled = false

    fun apply(speed: Float, snakeLength: String, colorsDisabled: Boolean) {
        this.speed = speed
        this.snakeLength = snakeLength
        this.colorsDisabled = colorsDisabled
    }

    fun discard() {
        speed = 0.5f
        snakeLength = "normal"
        colorsDisabled = false
    }

    private fun castLength(): String {
        when (snakeLength) {
            "small" -> return "5"
            "normal" -> return "12"
            "long" -> return "16"
            "super long" -> return "20"
        }
        return "6"
    }

    fun getSettings(): String = """{
        |"speed":${11 - (speed * 10)}, 
        |"snakeLength":${castLength()}, 
        |"colorsDisabled":$colorsDisabled
        |}""".trimMargin()
}