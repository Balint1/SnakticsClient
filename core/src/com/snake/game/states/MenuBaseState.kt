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

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {}

    override fun render(sb: SpriteBatch) {
        stage.act()
        stage.draw()
    }

    override fun update(dt: Float) {}

    override fun dispose() {}

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    protected fun createTextButton(name: String, width: Float = ELEMENT_WIDTH, height: Float = ELEMENT_HEIGHT, onClick: () -> Unit): TextButton {
        val button = TextButton(name, skin)
        button.setSize(width, height)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }

    protected fun setTitle(title: String) {
        Label(title, skin, "title").apply {
            setSize(ELEMENT_WIDTH, ELEMENT_HEIGHT)
            setAlignment(Align.center)
            setFontScale(2f)
            addElement(this)
        }
    }

    protected fun addElement(actor: Actor, padTop: Float = SPACING, parent: Table = rootTable) {
        if (parent.rows > 0)
            parent.add(actor).width(actor.width).height(actor.height).padTop(padTop).expandX()
        else
            parent.add(actor).width(actor.width).height(actor.height).expandX()

        parent.row()
    }

    protected fun addElements(vararg actors: Actor, spacing: Float = 0f, padTop: Float = SPACING, parent: Table = rootTable) {
        val table = Table()
        if (DEBUG_LAYOUT)
            table.debug()

        var pad = spacing
        var height = 0f
        for (a in actors) {
            if (a == actors.last())
                pad = 0f
            table.add(a).width(a.width).height(a.height).padRight(pad)
            height = height.coerceAtLeast(a.height)
        }
        table.setSize(ELEMENT_WIDTH, height)
        addElement(table, padTop, parent)
    }

    companion object {
        const val DEBUG_LAYOUT = true

        const val MIN_WORLD_WIDTH = 1600f
        const val MIN_WORLD_HEIGHT = 900f

        const val ELEMENT_WIDTH = MIN_WORLD_WIDTH / 2f
        const val ELEMENT_HEIGHT = ELEMENT_WIDTH / 7f
        const val SPACING = ELEMENT_WIDTH / 10f
    }
}