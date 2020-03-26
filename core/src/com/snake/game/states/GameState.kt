package com.snake.game.states

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.snake.game.singletons.sockets.Data
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.SocketService

class GameState : BaseGameState() {
    private val slider: Slider = Slider(-3f, 3f, 1f, false, skin)

    init {
        slider.value = 0f
        slider.touchable = Touchable.enabled
        slider.setPosition(20f, 300f)
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                SocketService.socket.emit(Events.SLIDER_CHANGE, Data.SLIDER_CHANGE(slider.value.toInt()))
                println(slider.value)
            }
        })
        stage.addActor(slider)
    }
}