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
    private val buttonWidth = width / 5
    private val mainTable = Table()
    private val fireballTexture = Texture("powerUpsSprites/fireBallButton.png")
    private val throughWallsTexture = Texture("powerUpsSprites/throughWallsButton.png")
    private var fireBallCountNum: Int = 0
    private var throughWallsCountNum: Int = 0

    private val fireBallCount = Label(fireBallCountNum.toString(), skin, "big").apply {
        setAlignment(Align.center)
    }
    private val throughWallsCount = Label(throughWallsCountNum.toString(), skin, "big").apply {
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
        mainTable.add(fireBallTable).padLeft(buttonWidth).padRight(buttonWidth)
        mainTable.add(throughWallsTable).padLeft(buttonWidth).padRight(buttonWidth)
    }

    fun getPowerupsControlPanel(): Table {
        return mainTable
    }

    private fun createImageButton(texture: Texture, width: Float, height: Float, onClick: () -> Unit): ImageButton {
        val drawable: Drawable = TextureRegionDrawable(TextureRegion(texture))
        val button = ImageButton(drawable)
        button.setSize(width, height)
        button.touchable = Touchable.disabled
        button.isDisabled = true
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                onClick()
            }
        })
        return button
    }

    fun pickup(powerup: TagComponent.EntityTagType) {
        if (powerup == TagComponent.EntityTagType.Fireball) {
            fireButton.touchable = Touchable.enabled
            fireBallCountNum++
            fireBallCount.setText(fireBallCountNum.toString())
        } else if (powerup == TagComponent.EntityTagType.ThroughWalls) {
            throughWallsButton.touchable = Touchable.enabled
            throughWallsCountNum++
            throughWallsCount.setText(throughWallsCountNum.toString())
        }
    }

    private fun shootFireball() {
        println("Shoot fireball")
        fireBallCountNum--
        if(fireBallCountNum == 0){
            fireButton.touchable = Touchable.disabled
        }
        fireBallCount.setText(fireBallCountNum.toString())
        SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP(TagComponent.EntityTagType.Fireball.typeString))
    }

    private fun activateThroughWalls() {
        println("Through th Walls")
        throughWallsCountNum--
        if(throughWallsCountNum == 0){
            throughWallsButton.touchable = Touchable.disabled
        }
        throughWallsCount.setText(throughWallsCountNum.toString())
        SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP("through-walls"))
    }
}