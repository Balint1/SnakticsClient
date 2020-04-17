package com.snake.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import com.snake.game.backend.SocketService
import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.states.MainMenu
import com.snake.game.states.StateManager

class SnakeGame : ApplicationAdapter() {
    private var batch: SpriteBatch? = null

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()

        StateManager.push(MainMenu())
    }

    override fun render() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch!!.begin()
        StateManager.update(Gdx.graphics.deltaTime)
        StateManager.render(batch!!)
        batch!!.end()
    }

    override fun dispose() {
        super.dispose()
        batch?.dispose()
        SocketService.socket.disconnect()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        StateManager.resize(width, height)
    }
}
