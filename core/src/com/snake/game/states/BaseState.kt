package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.snake.game.controls.SwipeDetector

abstract class BaseState : IState {
    protected val stage = Stage(ExtendViewport(MenuBaseState.VIRTUAL_WIDTH, MenuBaseState.VIRTUAL_HEIGHT))
    private val multiplexer = InputMultiplexer()

    protected val skin = Skin(Gdx.files.internal("skins/comic/comic-ui.json"))

    override fun activated() {
        multiplexer.addProcessor(SwipeDetector)
        multiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = multiplexer
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

    protected fun createImageButton(texture: Texture, width: Float = MenuBaseState.ELEMENT_WIDTH, height: Float = MenuBaseState.ELEMENT_HEIGHT, isDisabled: Boolean = false, onClick: () -> Unit): ImageButton {
        val drawable: Drawable = TextureRegionDrawable(TextureRegion(texture))
        val button = ImageButton(drawable)
        button.setSize(width, height)
        button.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
        button.isDisabled = isDisabled
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }

    protected fun createAlertDialog(text: String, optionYes: String, optionNO: String, onYes: () -> Unit, onNo: () -> Unit): Dialog{
        return (object : Dialog("", skin, "default") {
            override fun result(result: Any) {
                println("Result: $result")
            }
        }).apply<Dialog> {
            contentTable.add(Label(text, skin, "big"))
            button(optionYes, true)
            button(optionNO, false)
            isMovable = false
            isResizable = false
        }
    }
}