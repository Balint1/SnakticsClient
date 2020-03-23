package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Base class for menu states providing common functionality for creating menus
 *
 */
abstract class MenuBaseState : IState {
    protected val stage: Stage = Stage(ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT))
    protected val skin = Skin(Gdx.files.internal("skins/comic/comic-ui.json"))

    var rootTable = Table()

    init {
        rootTable.setFillParent(true)
        rootTable.width = ELEMENT_WIDTH
        stage.addActor(rootTable)

        if (DEBUG_LAYOUT)
            rootTable.debug()
    }

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

    /**
     * Creates a button with the provided text, size and on click event
     *
     * @param text The text on the button
     * @param width The width of the button
     * @param height The height of the button
     * @param onClick The function called when the button is clicked
     * @return The created button
     */
    protected fun createTextButton(text: String, width: Float = ELEMENT_WIDTH, height: Float = ELEMENT_HEIGHT, onClick: () -> Unit): TextButton {
        val button = TextButton(text, skin)
        button.setSize(width, height)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }

    /**
     * Adds a label with the "title" style and the provided text
     *
     * @param title The text in the title
     */
    protected fun setTitle(title: String) {
        Label(title, skin, "title").apply {
            setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            setAlignment(Align.center)
            setFontScale(2f)
            addElement(this)
        }
    }

    /**
     * Adds an actor to the layout table of the menu
     *
     * @param actor The actor to be added
     * @param padTop Padding to the element above, always set to 0f for the first element
     * @param parent The table the element is added to, defaults to the root table of the menu
     */
    protected fun addElement(actor: Actor, padTop: Float = SPACING, parent: Table = rootTable) {
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
    protected fun addElements(vararg actors: Actor, spacing: Float = 0f, padTop: Float = SPACING, parent: Table = rootTable) {
        val table = Table()
        if (DEBUG_LAYOUT)
            table.debug()

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

    companion object {
        const val DEBUG_LAYOUT = false

        const val MIN_WORLD_WIDTH = 1600f
        const val MIN_WORLD_HEIGHT = 900f

        const val ELEMENT_WIDTH = MIN_WORLD_WIDTH / 2f
        const val ELEMENT_HEIGHT = ELEMENT_WIDTH / 7f
        const val SPACING = ELEMENT_WIDTH / 10f
    }
}