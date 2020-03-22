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

    fun getSocket(): Socket?{
        return socket
    }
    private fun configSocketEvents() {
        socket!!.on(Socket.EVENT_CONNECT) {
            Gdx.app.log("SocketIO", "Connected")
            socket!!.emit(Events.JOIN_REQUEST, Data.JOIN_REQUEST("my_nickname", "835aee55-3274-9f4f-dac5-87fb41f276f7"))
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
                        ///val state: JSONObject = data.getJSONObject("state")
                        Gdx.app.log("SocketIO", "state: $data")
                    } catch (e: JSONException) {
                        Gdx.app.log("SocketIO", "Error getting attributes: $e")
                    }
                }

    }
}
