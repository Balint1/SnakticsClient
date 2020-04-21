package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport

abstract class BaseState() : IState {
    protected val stage = Stage(ExtendViewport(MenuBaseState.VIRTUAL_WIDTH, MenuBaseState.VIRTUAL_HEIGHT))

    protected val skin = Skin(Gdx.files.internal("skins/comic/comic-ui.json"))

    override fun activated() {
        Gdx.input.inputProcessor = stage
    }

    override fun deactivated() {}

    override fun render(sb: SpriteBatch) {
        stage.viewport.apply()
        stage.act()
        stage.draw()
    }

    override fun update(dt: Float) {}

    override fun dispose() {}

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    /**
     * Creates a button with the provided text, size and on click event
     *
     * @param text The text on the button
     * @param width The width of the button
     * @param height The height of the button
     * @param onClick The function called when the button is clicked
     * @return The created button
     */
    protected fun createTextButton(text: String, width: Float = MenuBaseState.ELEMENT_WIDTH, height: Float = MenuBaseState.ELEMENT_HEIGHT, isDisabled: Boolean = false, onClick: () -> Unit): TextButton {
        val button = TextButton(text, skin)
        button.setSize(width, height)
        button.name = text
        button.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
        button.isDisabled = isDisabled
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }
}