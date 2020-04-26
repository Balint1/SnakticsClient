package com.snake.game.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup

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

    fun addBackButton(button: TextButton) {
        backButton = button
        rootTable.add(backButton)
    }
}
