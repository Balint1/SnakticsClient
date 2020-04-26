package com.snake.game.controls

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService
import com.snake.game.ecs.component.TagComponent

class PowerupsPanel(skin: Skin, width: Float) {

    private val mainTable = Table()
    private val fireballTexture = Texture("powerUpsSprites/fireBallButton.png")
    private val throughWallsTexture = Texture("powerUpsSprites/throughWallsButton.png")

    init {
        val BUTTON_WIDTH = width / 5

        val fireButton: ImageButton = createImageButton(fireballTexture, BUTTON_WIDTH, BUTTON_WIDTH) {
            println("Shoot fireball")
            SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP(TagComponent.EntityTagType.Fireball.typeString))
        }
        val throughWallsButton: ImageButton = createImageButton(throughWallsTexture, BUTTON_WIDTH, BUTTON_WIDTH) {
            println("Through th Walls")
            SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP("through-walls"))
        }

        val fireBallCount = Label("0", skin, "big").apply {
            setAlignment(Align.center)
        }
        val throughWallsCount = Label("0", skin, "big").apply {
            setAlignment(Align.center)
        }

        val fireBallTable = Table().apply {
            add(fireButton).width(BUTTON_WIDTH).height(BUTTON_WIDTH).align(Align.center)
            row()
            add(fireBallCount).width(BUTTON_WIDTH).height(BUTTON_WIDTH).align(Align.center)
        }

        val throughWallsTable = Table().apply {
            add(throughWallsButton).width(BUTTON_WIDTH).height(BUTTON_WIDTH).align(Align.center)
            row()
            add(throughWallsCount).width(BUTTON_WIDTH).height(BUTTON_WIDTH).align(Align.center)
        }
        mainTable.add(fireBallTable).padLeft(BUTTON_WIDTH).padRight(BUTTON_WIDTH)
        mainTable.add(throughWallsTable).padLeft(BUTTON_WIDTH).padRight(BUTTON_WIDTH)
    }

    fun getPowerupsControlPanel(): Table {
        return mainTable
    }

    private fun createImageButton(texture: Texture, width: Float, height: Float, isDisabled: Boolean = false, onClick: () -> Unit): ImageButton {
        val drawable: Drawable = TextureRegionDrawable(TextureRegion(texture))
        val button = ImageButton(drawable)
        button.setSize(width, height)
        button.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
        button.isDisabled = isDisabled
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }
}