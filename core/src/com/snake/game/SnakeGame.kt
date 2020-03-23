package com.snake.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.snake.game.sockets.Slider

import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.sockets.SocketService

class SnakeGame : ApplicationAdapter() {

    private var batch: SpriteBatch? = null
    private var slider: Slider? = null

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()
        SocketService.start()
        slider = Slider()

        var ecs = SnakeECSEngine
    }

    override fun render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch!!.begin()
        slider!!.render()
        batch!!.end()
    }
}
