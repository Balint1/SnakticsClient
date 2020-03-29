package com.snake.game.singletons.sockets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Timer
import com.snake.game.Config.BASE_URL
import com.snake.game.states.MainMenu
import com.snake.game.states.StateManager
import io.socket.client.IO
import io.socket.client.Socket

object SocketService {
    var socket: Socket = IO.socket(BASE_URL)
    var listeners: MutableMap<String, Boolean> = mutableMapOf(
            Events.JOIN_RESPONSE to false,
            Events.UPDATE to false,
            Events.NEW_PLAYER to false
    )
    private var timeCount: Int = 0
    private const val timeout: Int = 10
    private var timerTask: Timer.Task? = null

    fun tryConnect(onTryingConnect: (Boolean, Int) -> Unit) {
        if (!socket.connected()) {
            socket.connect()
            timerTask = object : Timer.Task() {
                override fun run() {
                    Gdx.app.postRunnable {
                        onTryingConnect(true, timeout - timeCount)
                    }
                    if (timeCount >= timeout - 1) {
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

    fun joinRoom(nickname: String, roomId: String, password: String) {
        socket.emit(Events.JOIN_REQUEST, Data.JOIN_REQUEST(nickname, roomId, password))
    }
}
