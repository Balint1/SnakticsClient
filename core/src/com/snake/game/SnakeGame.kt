package com.snake.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.singletons.http.HttpService
import com.snake.game.singletons.sockets.SocketService
import com.snake.game.states.MainMenu
import com.snake.game.states.StateManager

class SnakeGame : ApplicationAdapter() {
    private var batch: SpriteBatch? = null

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        batch = SpriteBatch()
        SocketService.start()

        var ecs = SnakeECSEngine

        StateManager.push(MainMenu())

        HttpService().getRooms()
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
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        StateManager.resize(width, height)
    }
}
