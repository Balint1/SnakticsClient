package com.snake.game.controls

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService
import com.snake.game.ecs.component.TagComponent

class ItemPowerups(width: Float, height: Float) {
    private val buttonWidth = width / 5
    private val mainTable = Table()
    private val fireballTexture = Texture("powerup-sprites/powerup-fire-ball-32bits.png")
    private val throughWallsTexture = Texture("powerup-sprites/powerup-tw-32bits.png")
    private val redLabelStyle = Label.LabelStyle(BitmapFont(), Color.RED)
    private val greenLabelStyle = Label.LabelStyle(BitmapFont(), Color.GREEN)

    private val fireBallCount = Label("0", redLabelStyle).apply {
        setAlignment(Align.center)
    }
    private val throughWallsCount = Label("0", redLabelStyle).apply {
        setAlignment(Align.center)
    }

    private val fireButton: ImageButton = createImageButton(fireballTexture, buttonWidth, buttonWidth) {
        shootFireball()
    }

    private val throughWallsButton: ImageButton = createImageButton(throughWallsTexture, buttonWidth, buttonWidth) {
        activateThroughWalls()
    }

    init {
        val fireBallTable = Table().apply {
            add(fireButton).width(buttonWidth).height(buttonWidth).align(Align.center)
            row()
            add(fireBallCount).width(buttonWidth).height(buttonWidth).align(Align.center)
        }

        val throughWallsTable = Table().apply {
            add(throughWallsButton).width(buttonWidth).height(buttonWidth).align(Align.center)
            row()
            add(throughWallsCount).width(buttonWidth).height(buttonWidth).align(Align.center)
        }
        mainTable.apply {
            padTop(height / 3)
            add(fireBallTable).padLeft(buttonWidth).padRight(buttonWidth)
            add(throughWallsTable).padLeft(buttonWidth).padRight(buttonWidth)
            padBottom(height / 4)
        }
    }

    fun getPowerupsControlPanel(): Table {
        return mainTable
    }

    private fun createImageButton(texture: Texture, width: Float, height: Float, onClick: () -> Unit): ImageButton {
        val drawable: Drawable = TextureRegionDrawable(TextureRegion(texture))
        val button = ImageButton(drawable)
        button.setSize(width, height)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }

    fun updateFb(fireballCount: Int) {
        if (fireballCount > 0) {
            fireBallCount.style = greenLabelStyle
        } else if (fireballCount == 0) {
            fireBallCount.style = redLabelStyle
        }
        fireBallCount.setText(fireballCount.toString())
    }

    fun updateTw(twCount: Int) {
        if (twCount > 0) {
            throughWallsCount.style = greenLabelStyle
        } else if (twCount == 0) {
            throughWallsCount.style = redLabelStyle
        }
        throughWallsCount.setText(twCount.toString())
    }

    private fun shootFireball() {
        SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP(TagComponent.EntityTagType.Fireball.typeString))
    }

    private fun activateThroughWalls() {
        SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP("through-walls"))
    }
}