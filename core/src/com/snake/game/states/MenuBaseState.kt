package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Label

import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ExtendViewport

/**
 * Base class for menu states providing common functionality for creating menus
 *
 */
abstract class MenuBaseState : BaseState(Stage(ExtendViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT))) {
    var rootTable = Table()

    init {
        rootTable.setFillParent(true)
        rootTable.width = ELEMENT_WIDTH
        stage.addActor(rootTable)
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
     */
    protected fun addElement(actor: Actor, padTop: Float = SPACING) {
        addElement(actor, padTop, rootTable)
    }

    /**
     * Adds a number of elements on the same row to the layout table of the menu
     *
     * @param actors The actors to be added
     * @param spacing The horizontal spacing between the actors
     * @param padTop Padding to the element above, always set to 0f for the first element
     */
    protected fun addElements(vararg actors: Actor, spacing: Float = 0f, padTop: Float = SPACING) {
        addElements(*actors, spacing = spacing, padTop = padTop, parent = rootTable)
    }


    protected fun isAlphaNumeric(text: String): Boolean {
        return text.matches("^[a-zA-Z0-9]*$".toRegex())
    }
}