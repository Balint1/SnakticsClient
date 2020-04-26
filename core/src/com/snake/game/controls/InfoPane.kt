package com.snake.game.controls

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.utils.Align

class InfoPane(pseudoStage: VerticalGroup, val width: Float, val height: Float) {
    private val rootTable = Table()

    init {
        pseudoStage.addActor(rootTable)
    }

    fun addRow(actor: Actor) {
        rootTable.apply {
            add(actor)
            row()
        }
    }

    fun showDeathMessage(){
        val redLabelStyle = Label.LabelStyle(BitmapFont(), Color.RED)
        val deathMessage = Label("You died. you can stay and watch the game or go to lobby", redLabelStyle).apply {
            setAlignment(Align.center)
        }
        rootTable.clear()

    }
}
