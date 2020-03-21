package com.snake.game.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align

class MainMenu : MenuBaseState() {

    init {
        val title = Label("Snaktics", skin, "title")
        title.setSize(BUTTON_WIDTH, BUTTON_HEIGHT)
        title.setAlignment(Align.center)
        title.setFontScale(2f)
        addMenuElement(title)

        val joinButton = createTextButton("Join Room", skin)
        joinButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //StateManager.push(RoomList)
            }
        })
        addMenuElement(joinButton)

        val createButton = createTextButton("Create Room", skin)
        createButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //StateManager.push(CreateRoom())
            }
        })
        addMenuElement(createButton)

        val settingsButton = createTextButton("Settings", skin)
        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                //StateManager.push(Settings())
            }
        })
        addMenuElement(settingsButton)
    }

    override fun render(sb: SpriteBatch) {
        stage.act()
        stage.draw()
    }

    override fun update(dt: Float) {}

    override fun dispose() {
    }
}