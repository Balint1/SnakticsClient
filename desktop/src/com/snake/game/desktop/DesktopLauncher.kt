package com.snake.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.snake.game.SnakeGame
import com.snake.game.states.MenuBaseState.Companion.MIN_WORLD_HEIGHT
import com.snake.game.states.MenuBaseState.Companion.MIN_WORLD_WIDTH

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = MIN_WORLD_WIDTH.toInt()
        config.height = MIN_WORLD_HEIGHT.toInt()
        LwjglApplication(SnakeGame(), config)
    }
}