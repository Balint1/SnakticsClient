package com.snake.game.singletons.sockets

import com.badlogic.gdx.Gdx
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

object SocketService {

    var socket: Socket = IO.socket("http://localhost:5000")

    fun start() {
        connectSocket()
        configSocketEvents()
    }

    private fun connectSocket() {
        try {
            socket.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT) {
            Gdx.app.log("SocketIO", "Connected with id ${socket.id()}")
        }
                .on(Events.JOIN_SUCCEEDED) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        val message: String = data.getString("message")
                        val id: String = data.getString("id")
                        Gdx.app.log("SocketIO", "$message,  socket id: $id")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attributes: $e")
                    }
                }.on(Events.JOIN_FAILED) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        val message: String = data.getString("message")
                        val id: String = data.getString("id")
                        Gdx.app.log("SocketIO", "$message, socket id: $id")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attributes: $e")
                    }
                }.on(Events.NEW_PLAYER) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        val id: String = data.getString("id")
                        Gdx.app.log("SocketIO", "New Player Connected: $id")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attributes: $e")
                    }
                }.on(Events.UPDATE) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        // /val state: JSONObject = data.getJSONObject("state")
                        Gdx.app.log("SocketIO", "state: $data")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attributes: $e")
                    }
                }
    }
}
