package com.snake.game.controls

import com.badlogic.gdx.input.GestureDetector
import com.snake.game.backend.Data
import com.snake.game.backend.Events
import com.snake.game.backend.SocketService

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
