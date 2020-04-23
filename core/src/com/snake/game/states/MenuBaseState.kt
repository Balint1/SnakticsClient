package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label

import com.badlogic.gdx.utils.Align

/**
 * Base class for menu states providing common functionality for creating menus
 *
 */
abstract class MenuBaseState : BaseState() {
    var rootTable = Table()
    var dialog: Dialog? = null

    init {
        rootTable.setFillParent(true)
        rootTable.width = ELEMENT_WIDTH
        stage.addActor(rootTable)

        if (DEBUG_LAYOUT)
            rootTable.debug()
    }

    /**
     * Creates a scroll pane with the provided list, size and scrollbar options
     *
     * @param list A list to be displayed in pane
     * @param width The width of the button
     * @param height The height of the button
     * @param bottom parameter for setScrollBarPositions
     * @param right parameter for setScrollBarPositions
     * @return The scroll pane
     */
    protected fun createScrollPane(list: Table, width: Float = ELEMENT_WIDTH, height: Float = ELEMENT_HEIGHT * 4, bottom: Boolean = true, right: Boolean = true): ScrollPane {
        val scrollPane = ScrollPane(list)
        scrollPane.setSize(width, height)
        scrollPane.setScrollBarPositions(bottom, right)
        return scrollPane
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

    /**
     * Shows a dialog
     *
     * @param text The text in the dialog
     */
    protected fun showWaitDialog(text: String) {
        dialog = (object : Dialog("", skin, "default") {
        }).apply {
            addElement(Label(text, skin, "big"), parent = contentTable)
            pad(SPACING / 5f)
            isMovable = false
            isResizable = false
            if (DEBUG_LAYOUT)
                contentTable.debug()
            show(this@MenuBaseState.stage)
        }
    }

    /**
     * Shows a dialog with a button
     *
     * @param text The text in the dialog
     * @param buttonText The text on the button
     * @param onResult Function called when the button is clicked
     */
    protected fun showMessageDialog(text: String, buttonText: String = "Ok", onResult: () -> Unit = {}) {
        dialog = (object : Dialog("", skin, "default") {
            override fun result(result: Any) {
                onResult()
            }
        }).apply {
            addElement(Label(text, skin, "big"), parent = contentTable)
            button(buttonText, true)
            pad(SPACING / 5f)
            isMovable = false
            isResizable = false
            if (DEBUG_LAYOUT)
                contentTable.debug()
            show(this@MenuBaseState.stage)
        }
    }

    protected fun hideDialog() {
        dialog?.hide(null)
    }

    companion object {
        const val DEBUG_LAYOUT = false

        const val VIRTUAL_WIDTH = 1600f
        const val VIRTUAL_HEIGHT = 900f

        const val ELEMENT_WIDTH = VIRTUAL_WIDTH / 2f
        const val ELEMENT_HEIGHT = ELEMENT_WIDTH / 7f
        const val SPACING = ELEMENT_WIDTH / 10f

        const val ICON_WIDTH = ELEMENT_WIDTH / 25
    }
}