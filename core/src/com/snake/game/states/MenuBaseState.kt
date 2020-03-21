package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ExtendViewport


abstract class MenuBaseState : IState {
    protected val stage: Stage = Stage(ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT))
    protected val skin = Skin(Gdx.files.internal("skins/comic/comic-ui.json"))

    var table = Table()

    init {
        Gdx.input.inputProcessor = stage
        table.setFillParent(true);
        stage.addActor(table);

        // Debug layout
        //table.debug()
    }

    override fun render(sb: SpriteBatch) {
        stage.act()
        stage.draw()
    }

    protected fun createTextButton(name: String, skin: Skin): TextButton {
        val button = TextButton(name, skin)
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT)
        return button
    }

    protected fun addMenuElement(actor: Actor) {
        if(table.rows > 0)
            table.add(actor).width(actor.width).height(actor.height).padTop(SPACING).expandX();
        else
            table.add(actor).width(actor.width).height(actor.height).expandX();

        table.row()
    }

    companion object {
        val SCREEN_WIDTH = Gdx.graphics.width
        val SCREEN_HEIGHT = Gdx.graphics.height

        const val MIN_WORLD_WIDTH = 1600f
        const val MIN_WORLD_HEIGHT = 900f

        const val BUTTON_WIDTH = MIN_WORLD_WIDTH / 2f
        const val BUTTON_HEIGHT = BUTTON_WIDTH / 7f
        const val SPACING = BUTTON_WIDTH / 10f
    }
}