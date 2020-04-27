package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.snake.game.ecs.SnakeECSEngine

class GameWidget(
    val ecs: SnakeECSEngine,
    private val fieldWidth: Float,
    private val fieldHeight: Float,
    private val standardViewport: Viewport
) : Widget() {

    // private val viewport = ExtendViewport(fieldWidth, fieldHeight, fieldWidth, fieldHeight, camera)
    private val viewport = FitViewport(fieldWidth, fieldHeight)

    // The SpriteBatch used for rendering the game
    private val sb = SpriteBatch()

    private fun updateSize() {
        viewport.update(width.toInt(), height.toInt())
        viewport.screenX = Gdx.graphics.width - width.toInt()
    }

    fun render() {
        // TODO Would be better to only update size when necessary, but for some reason it doesn't work too well
        updateSize()

        viewport.apply(true)
        sb.projectionMatrix = viewport.camera.combined

        ecs.render(sb, fieldWidth, fieldHeight)

        standardViewport.apply(true)
    }
}
