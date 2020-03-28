package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.snake.game.singletons.sockets.Data
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.SocketService
import org.json.JSONException
import org.json.JSONObject

class GameState : BaseGameState() {
    private val slider: Slider = Slider(-3f, 3f, 1f, false, skin)

    init {
        addListeners()
        slider.value = 0f
        slider.touchable = Touchable.enabled
        slider.setPosition(20f, 300f)
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                    emitSliderValue(slider.value.toInt())
            }
        })
        stage.addActor(slider)
    }

    private fun emitSliderValue(value: Int) {
        SocketService.socket.emit(Events.SLIDER_CHANGE, Data.SLIDER_CHANGE(value))
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE) { args ->
            onStateUpdate(args)
        }
    }

    private fun onStateUpdate(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        try {
            val state = data.getJSONArray("state")
            Gdx.app.log("SocketIO", "state: $state")
        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }
}