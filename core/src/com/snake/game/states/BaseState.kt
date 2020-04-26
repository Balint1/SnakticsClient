package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.Viewport
import com.snake.game.controls.SwipeDetector

abstract class BaseState(protected val stage: Stage) : IState {
    protected val multiplexer = InputMultiplexer()
    var dialog: Dialog? = null

    override fun activated() {
        val multiplexer = InputMultiplexer()
        multiplexer.addProcessor(SwipeDetector)
        multiplexer.addProcessor(stage)
        Gdx.input.inputProcessor = multiplexer

        //stage.isDebugAll = true
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

    fun getViewport() : Viewport {
        return stage.viewport
    }

    override fun onBackPressed() {
        if(StateManager.numStates() == 1) {
            showButtonDialog("Are you sure you want to exit the game?", "Yes", "No") {
                if (it == 0)
                    super.onBackPressed()
            }
        }
        else
        {
            super.onBackPressed()
        }
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
    protected fun createTextButton(text: String, width: Float = BaseState.ELEMENT_WIDTH, height: Float = BaseState.ELEMENT_HEIGHT, isDisabled: Boolean = false, onClick: () -> Unit): TextButton {
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

    /**
     * Shows a dialog
     *
     * @param text The text in the dialog
     */
    protected fun showWaitDialog(text: String) {
        dialog = (object : Dialog("", skin, "default") {
        }).apply {
            addElement(Label(text, skin, "big"), parent = contentTable)
            pad(BaseState.SPACING / 5f)
            isMovable = false
            isResizable = false
            show(this@BaseState.stage)
        }
    }

    /**
     * Shows a dialog with a button
     *
     * @param text The text in the dialog
     * @param buttons The text on the buttons
     * @param onResult Function called when the button is clicked. The parameter is set to the index of the button that was clicked
     */
    protected fun showButtonDialog(text: String, vararg buttons: String = arrayOf("Ok"), onResult: (Int) -> Unit = {}) {
        dialog = (object : Dialog("", skin, "default") {
            override fun result(result: Any) {
                if (result is Int)
                    onResult(result)
            }
        }).apply {
            addElement(Label(text, skin, "big"), parent = contentTable)
            for (i in buttons.indices)
                button(buttons.get(i), i)
            for (i in 0 until buttons.size - 1)
                buttonTable.cells[i].padRight(BaseState.SPACING / 2f)
            pad(BaseState.SPACING / 5f)
            isMovable = false
            isResizable = false
            show(this@BaseState.stage)
        }
    }

    protected fun hideDialog() {
        dialog?.hide(null)
    }

    /**
     * Adds an actor to the layout table of the menu
     *
     * @param actor The actor to be added
     * @param padTop Padding to the element above, always set to 0f for the first element
     * @param parent The table the element is added to, defaults to the root table of the menu
     */
    protected fun addElement(actor: Actor, padTop: Float = SPACING, parent: Table) {
        if (parent.rows > 0)
            parent.add(actor).width(actor.width).height(actor.height).padTop(padTop).expandX()
        else
            parent.add(actor).width(actor.width).height(actor.height).expandX()

        parent.row()
    }

    /**
     * Adds a number of elements on the same row to the layout table of the menu
     *
     * @param actors The actors to be added
     * @param spacing The horizontal spacing between the actors
     * @param padTop Padding to the element above, always set to 0f for the first element
     * @param parent The table the elements are added to, defaults to the root table of the menu
     */
    protected fun addElements(vararg actors: Actor, spacing: Float = 0f, padTop: Float = SPACING, parent: Table) {
        val table = Table()
        var pad = spacing
        var height = 0f
        var width = 0f
        for (a in actors) {
            if (a == actors.last())
                pad = 0f
            table.add(a).width(a.width).height(a.height).padRight(pad)
            width += a.width + pad
            height = height.coerceAtLeast(a.height)
        }
        table.setSize(width, height)
        addElement(table, padTop, parent)
    }

    protected fun createImageButton(texture: Texture, width: Float = BaseState.ELEMENT_WIDTH, height: Float = BaseState.ELEMENT_HEIGHT, isDisabled: Boolean = false, onClick: () -> Unit): ImageButton {
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

    companion object {
        const val VIRTUAL_WIDTH = 1600f
        const val VIRTUAL_HEIGHT = 900f

        const val ELEMENT_WIDTH = VIRTUAL_WIDTH / 2f
        const val ELEMENT_HEIGHT = ELEMENT_WIDTH / 7f
        const val SPACING = ELEMENT_WIDTH / 10f

        const val ICON_WIDTH = ELEMENT_WIDTH / 25
        val skin = Skin(Gdx.files.internal("skins/comic/comic-ui.json"))
    }
}