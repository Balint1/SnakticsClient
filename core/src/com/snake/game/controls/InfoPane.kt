package com.snake.game.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align

class InfoPane(pseudoStage: VerticalGroup, val width: Float, val height: Float) {
    private val rootTable = Table()
    private var backButton: TextButton? = null

    init {
        pseudoStage.addActor(rootTable)
    }

    fun addRow(actor: Actor) {
        rootTable.apply {
            add(actor)
            row()
        }
    }

    fun showDeathMessage(skin: Skin) {
        rootTable.clear()
        val deathMessage = Label("You died", skin, "big").apply {
            width = this@InfoPane.width * 4 / 5
            setAlignment(Align.center)
        }
        rootTable.apply {
            padTop(height * 2 / 6)
            add(deathMessage).padBottom(height * 1 / 6)
            row()
            add(backButton)
        }
    }

    fun addBackButton(button: TextButton) {
        backButton = button
        rootTable.add(backButton)
    }
}
