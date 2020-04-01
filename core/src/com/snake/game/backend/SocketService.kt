package com.snake.game.backend

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Timer
import com.snake.game.Config.BASE_URL
import com.snake.game.Config.TIMEOUT
import com.snake.game.states.MainMenu
import com.snake.game.states.StateManager
import io.socket.client.IO
import io.socket.client.Socket

object SocketService {

    private var timeCount: Int = 0
    private var timerTask: Timer.Task? = null
    var socket: Socket = IO.socket(BASE_URL)
    var listeners: MutableMap<String, Boolean> = mutableMapOf(
            Events.JOIN_RESPONSE.value to false,
            Events.UPDATE.value to false,
            Events.NEW_PLAYER.value to false
    )

    /**
     * Tries to connect socket to the server within TIMEOUT seconds
     *
     * @param onTryingConnect a function that will execute on every tick of timer
     */
    fun tryConnect(onTryingConnect: (Boolean, Int) -> Unit) {
        if (!socket.connected()) {
            socket.connect()
            timerTask = object : Timer.Task() {
                override fun run() {
                    Gdx.app.postRunnable {
                        onTryingConnect(true, TIMEOUT - timeCount)
                    }
                    if (timeCount == TIMEOUT - 1) {
                        socket.disconnect()
                        timeCount = 0
                        cancel()
                        Gdx.app.postRunnable {
                            onTryingConnect(false, 0)
                        }
                    } else {
                        timeCount++
                    }
                }
            }
            Timer.schedule(timerTask, 1f, 1f)
        }
    }

    /**
     * Start listening to socket Connect and Disconnect events
     *
     * @param onSocketConnect a function that will execute is socket will successfully connected
     */
    fun addListeners(onSocketConnect: (Boolean, String) -> Unit) {
        socket.on(Socket.EVENT_CONNECT) {
            Gdx.app.log("SocketIO", "Connected with id ${socket.id()}")
            timeCount = 0
            timerTask?.cancel()
            Gdx.app.postRunnable {
                onSocketConnect(true, "Connected")
            }
        }.on(Socket.EVENT_DISCONNECT) {
            Gdx.app.log("SocketIO", "Disconnected")
            for (k in listeners.keys) {
                listeners[k] = false
            }
            Gdx.app.postRunnable {
                StateManager.set(MainMenu())
            }
        }
    }

    /**
     * Emits joi request
     *
     * @param nickname nickname of the user that tries to connect
     * @param roomId id of the room user tries to connect
     * @param password password for the room
     */
    fun joinRoom(nickname: String, roomId: String, password: String) {
        Gdx.app.log("SocketIO", "Request::joinRoom " +
                "socket: ${socket.id()}, " +
                "nickname: $nickname, " +
                "roomId: $roomId")
        socket.emit(Events.JOIN_REQUEST.value, Data.JOIN_REQUEST(nickname, roomId, password))
    }
}
