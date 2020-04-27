package com.snake.game.controls

import com.badlogic.gdx.input.GestureDetector
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService


/*
object that detect swipe
And send the swipe event to the server if activated (input-system.ts)
(based on the location of the mouse at the beginning and the and of the "mouse down" or touch)
to implement it a multiplexer as to be used (because the game use also inputs in stage)
multiplexer is possible because true is never send after input event therefore the next elements in
the multiplexer will receive them as if the swipeDetector does not exist
 */
object SwipeDetector : GestureDetector(DirectionGestureListener()) {
    var active: Boolean = false
    public fun up() {
        if (active) {
            SocketService.socket.emit(Events.SWIPE.value, Data.SWIPE("up"))
        }
    }
    public fun down() {
        if (active) {
            SocketService.socket.emit(Events.SWIPE.value, Data.SWIPE("down"))
        }
    }
    public fun left() {
        if (active) {
            SocketService.socket.emit(Events.SWIPE.value, Data.SWIPE("left"))
        }
    }
    public fun right() {
        if (active) {
            SocketService.socket.emit(Events.SWIPE.value, Data.SWIPE("right"))
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
