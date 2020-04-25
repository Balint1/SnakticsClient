package com.snake.game.controls

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService
import com.snake.game.ecs.component.TagComponent
import com.snake.game.states.BaseState
import com.snake.game.states.MenuBaseState

class PowerupsPanel(uiGroup: VerticalGroup) : BaseState() {

    private val mainTable = Table()
    private val fireballTexture = Texture("powerUpsSprites/fireBallButton.png")
    private val throughWallsTexture = Texture("powerUpsSprites/throughWallsButton.png")

    init {
        val BUTTON_WIDTH = MenuBaseState.ELEMENT_HEIGHT
        println(BUTTON_WIDTH)
        val fireButton: ImageButton = createImageButton(fireballTexture, BUTTON_WIDTH, BUTTON_WIDTH) {
            println("-------------------")
            println("Shoot fireball")
            println("-------------------")
            SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP(TagComponent.EntityTagType.Fireball.typeString))
        }
        val throughWallsButton: ImageButton = createImageButton(throughWallsTexture, BUTTON_WIDTH, BUTTON_WIDTH) {
            println("-------------------")
            println("Through th Walls")
            println("-------------------")
            SocketService.socket.emit(Events.USE_POWERUP.value, Data.USE_POWEUP("through-walls"))
        }

        val fireBallCount = Label("0", skin, "big")
        val throughWallsCount = Label("0", skin, "big")

        val fireBallTable = Table().apply {
            add(fireButton).width(BUTTON_WIDTH).height(BUTTON_WIDTH)
            row()
            add(fireBallCount).width(BUTTON_WIDTH).height(BUTTON_WIDTH)
        }

        val throughWallsTable = Table().apply {
            add(throughWallsButton).width(BUTTON_WIDTH).height(BUTTON_WIDTH)
            row()
            add(throughWallsCount).width(BUTTON_WIDTH).height(BUTTON_WIDTH)
        }
        mainTable.add(fireBallTable)
        mainTable.add(throughWallsTable)
        uiGroup.addActor(mainTable)
    }


}