package com.snake.game.states

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle


class JoystickInput() {
    val touchpad: Touchpad
    val touchpadStyle: TouchpadStyle
    val touchpadSkin: Skin

    init {
        val x: Float = 100f
        val y: Float = 100f
        val width: Float = 300f
        val height: Float = 300f
        val deadZoneRadius: Float = 30f


        val background = Texture("joystick/joystick_bg.jpg")
        val knob = Texture("joystick/joystick_bg.jpg")


        touchpadSkin = Skin()
        //Set background image
        touchpadSkin.add("touchBackground", background)
        //Set knob image
        touchpadSkin.add("touchKnob", knob)
        //Create TouchPad Style
        touchpadStyle = TouchpadStyle()
        //Apply the Drawables to the TouchPad Style
        touchpadStyle.background = touchpadSkin.getDrawable("touchBackground")
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob")
        //Create new TouchPad with the created style
        touchpad = Touchpad(deadZoneRadius, touchpadStyle)
        //setBounds(x,y,width,height)
        touchpad.setBounds(x, y, width, height)

    }
}
