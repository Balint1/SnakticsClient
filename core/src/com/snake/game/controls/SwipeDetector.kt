package com.snake.game.controls

import com.badlogic.gdx.input.GestureDetector
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService
import kotlin.math.abs


/**
 * Detects swipes and sends swipe events to the server if activated (input-system.ts)
 * To implement it, a multiplexer has to be used (multiplexer is possible because true is never
 * sent after input event therefore the next elements in the multiplexer will receive them as
 * if the swipeDetector does not exist.
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
            if (abs(velocityX) > abs(velocityY)) {
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
