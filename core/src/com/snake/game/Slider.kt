package com.snake.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.snake.game.singletons.sockets.Data
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.SocketService

class Slider {
    private val stage: Stage = Stage()
    private val skin = Skin(Gdx.files.internal("gdxSkins/comic/skin/comic-ui.json"))
    private val slider: Slider = Slider(-3f, 3f, 1f, false, skin)

    init {
        Gdx.input.inputProcessor = stage
        slider.value = 0f
        slider.touchable = Touchable.enabled
        slider.setPosition(20f, 300f)
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                SocketService.socket!!.emit(Events.SLIDER_CHANGE, Data.SLIDER_CHANGE(slider.value.toInt()))
                println(slider.value)
            }
        })
        slider.addListener(object : InputListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                println("Touch Down")
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                println("Touch up")

            }
        })
        stage.addActor(slider)
    }

    fun render() {
        stage.act()
        stage.draw()
    }
}