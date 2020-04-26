package com.snake.game.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup

class InfoPane(stage: VerticalGroup, val paneWidth: Float, val paneHeight: Float) {
    private val rootTable = Table()

    init{
        stage.addActor(rootTable)
    }

    fun addRow(actor: Actor) {
        rootTable.apply {
            add(actor)
            row()
        }
    }

}