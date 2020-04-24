package com.snake.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.snake.game.SnakeGame
import com.snake.game.states.BaseState.Companion.VIRTUAL_HEIGHT
import com.snake.game.states.BaseState.Companion.VIRTUAL_WIDTH

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = VIRTUAL_WIDTH.toInt()
        config.height = VIRTUAL_HEIGHT.toInt()
        // Anti-aliasing
        config.samples = 3
        LwjglApplication(SnakeGame(), config)
    }
}