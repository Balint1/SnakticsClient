package com.snake.game.states

import com.snake.game.singletons.http.HttpService
import com.snake.game.singletons.sockets.Events
import com.snake.game.singletons.sockets.Data
import com.snake.game.singletons.sockets.SocketService

class Lobby : MenuBaseState() {
    private var roomId: String = HttpService.roomId //will be changed to something better
    init {
        setTitle("Lobby")

        createTextButton("Play") {
            SocketService.socket.emit(Events.JOIN_REQUEST, Data.JOIN_REQUEST("my_nickname", roomId))
            StateManager.push(GameState())
        }.apply {
            addElement(this)
        }

        createTextButton("Leave") {
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }
}