package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.snake.game.backend.HttpService
import com.snake.game.backend.SimpleResponse
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService
import com.snake.game.ecs.SnakeECSEngine
import org.json.JSONException
import org.json.JSONObject

class GameState(private val roomId: String, private val playerId: String) : MenuBaseState() {
    private val slider: Slider = Slider(-3f, 3f, 1f, false, skin)

    private val ecs = SnakeECSEngine

    init {
        cancelListeners()
        addListeners()
        slider.value = 0f
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                emitSliderValue(slider.value.toInt())
            }
        })
        addElement(slider)
        createTextButton("back") {
            HttpService.leaveRoom(roomId, playerId, ::onGameLeft)
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }

    override fun activated() {
        super.activated()

        ecs.createEntities()
    }

    private fun emitSliderValue(value: Int) {
        SocketService.socket.emit(Events.SLIDER_CHANGE.value, Data.SLIDER_CHANGE(value))
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE.value) { args ->
            onStateUpdate(args)
        }
    }

    private fun cancelListeners() {
        SocketService.socket.off(Events.OWNER_CHANGED.value)
        SocketService.socket.off(Events.PLAYER_LEFT.value)
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

    override fun update(dt: Float) {
        super.update(dt)
        ecs.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        super.render(sb)
        ecs.render(sb)
    }

    /**
     * Called when the player success or fails to to leave the game
     *
     * @param response response fom create room http request
     */
    private fun onGameLeft(response: SimpleResponse) {
        Gdx.app.debug("UI", "GameState::onGameLeft(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.set(MainMenu())
        } else {
            showMessageDialog(response.message)
        }
    }
}