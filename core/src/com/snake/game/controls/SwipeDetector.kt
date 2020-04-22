package com.snake.game.controls

import com.badlogic.gdx.input.GestureDetector
import com.sun.org.apache.xpath.internal.functions.FuncFalse

object SwipeDetector : GestureDetector(DirectionGestureListener()) {
    var active: Boolean = false
    public fun up(){
        if(active) {
            println("SWIPE UP")
        }
    }
    public fun down(){
        if(active) {
            println("SWIPE DOWN")
        }
    }
    public fun left(){
        if(active) {
            println("SWIPE LEFT")
        }
    }
    public fun right(){
        if(active) {
            println("SWIPE RIGHT")
        }
    }
    private class DirectionGestureListener() : GestureAdapter() {
        override fun fling(velocityX: Float, velocityY: Float, button: Int): Boolean {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX > 0) {
                    SwipeDetector.right()
                } else {
                    SwipeDetector.left()
                }
            } else {
                if (velocityY > 0) {
                    SwipeDetector.down()
                } else {
                    SwipeDetector.up()
                }
            }
            return super.fling(velocityX, velocityY, button)
        }
    }
}
