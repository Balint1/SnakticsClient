package com.snake.game.controls

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup

class InfoPane {
    private val rootTable = Table()
    private val stage = VerticalGroup()

    init{
        stage.addActor(rootTable)
    }

    fun addRow(actor: Actor) {
        rootTable.apply {
            add(actor)
            row()
        }
    }

    fun getStage(): VerticalGroup{
        return stage
    }

}