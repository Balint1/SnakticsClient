package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ExtendViewport

abstract class BaseState(protected val stage: Stage) : IState {


    protected val skin = Skin(Gdx.files.internal("skins/comic/comic-ui.json"))

    override fun activated() {
        Gdx.input.inputProcessor = stage
    }

    override fun deactivated() {}

    override fun render(sb: SpriteBatch) {
        stage.act()
        stage.draw()
    }

    override fun update(dt: Float) {}

    override fun dispose() {}

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}