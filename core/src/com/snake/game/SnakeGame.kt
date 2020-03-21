package com.snake.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.snake.game.sockets.SocketService
import com.snake.game.states.MainMenu
import com.snake.game.states.StateManager

class SnakeGame : ApplicationAdapter() {
    private var batch: SpriteBatch? = null

    override fun create() {
        batch = SpriteBatch()
        SocketService.start()
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
    }
}
