package com.snake.game.sockets

import com.badlogic.gdx.Gdx
import org.json.JSONException
import org.json.JSONObject

import io.socket.client.IO
import io.socket.client.Socket

object SocketService {

    private var socket: Socket? = null

    fun start() {
        connectSocket()
        configSocketEvents()
    }

    private fun connectSocket() {
        try {
            socket = IO.socket("http://localhost:5000")
            socket!!.connect()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun configSocketEvents() {
        socket!!.on(Socket.EVENT_CONNECT) {
            Gdx.app.log("SocketIO", "Connected")
            socket!!.emit(Events.JOIN_REQUEST, Data.JOIN_REQUEST("my_nickname", "wsahx728y8qwfsg"))
        }
                .on(Events.JOIN_SUCCEEDED) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        val message: String = data.getString("message")
                        val id: String = data.getString("id")
                        Gdx.app.log("SocketIO", "$message,  socket id: $id")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attribute: $e")
                    }
                }.on(Events.JOIN_FAILED) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        val message: String = data.getString("message")
                        val id: String = data.getString("id")
                        Gdx.app.log("SocketIO", "$message, socket id: $id")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attribute: $e")
                    }
                }
                .on(Events.NEW_PLAYER) { args ->
                    val data: JSONObject = args[0] as JSONObject
                    try {
                        val id: String = data.getString("id")
                        Gdx.app.log("SocketIO", "New Player Connected: $id")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting New Player id: $e")
                    }
                }
    }
}
