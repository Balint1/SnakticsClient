package com.snake.game.backend

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import com.snake.game.Config.API_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import okhttp3.RequestBody
import okhttp3.FormBody
import java.io.IOException

object HttpService {

    private val client: OkHttpClient = OkHttpClient()
    private var canceled: Boolean = false

    /**
     * Gets a list of all rooms from the server using http request
     *
     * @param action a function that will execute when http response is received
     */
    fun getRooms(action: (GetRoomsResponse) -> Unit) {
        Gdx.app.log("Http", "Request::getRooms")
        canceled = false
        val request: Request = Request.Builder().url(API_URL).build()
        var responseObject: GetRoomsResponse
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!canceled) {
                    Gdx.app.log("Http", "Response::getRooms(failed), ${e.message!!}")
                    val failureResponse = GetRoomsResponse(false, e.message!!, mutableListOf())
                    Gdx.app.postRunnable {
                        action(failureResponse)
                    }
                    e.printStackTrace()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!canceled) {
                    Gdx.app.log("Http", "Response::getRooms(succeeded)")
                    if (response.isSuccessful) {
                        responseObject = Gson().fromJson(response.body?.string(), GetRoomsResponse::class.java)
                        Gdx.app.postRunnable {
                            action(responseObject)
                        }
                    }
                }
            }
        })
    }

    fun cancelGetRooms() {
        Gdx.app.log("Http", "Request::getRooms(canceled)")
        canceled = true
    }

    /**
     * Attempts to create a new room
     *
     * @param name name of the room
     * @param password optional password for the room
     * @param capacity maximum allowed players in room
     * @param action a function that will execute when http response is received
     */
    fun createRoom(name: String, password: String, capacity: Int, settings: String, action: (CreateRoomResponse) -> Unit) {
        Gdx.app.log("Http", "Request::createRoom(name=$name, pw=$password, cap=$capacity)")
        val requestBody: RequestBody = FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("capacity", capacity.toString())
                .add("ownerId", SocketService.socket.id())
                .add("settings", settings)
                .build()

        val request: Request = Request.Builder()
                .url("$API_URL/create")
                .post(requestBody)
                .build()

        var responseObject: CreateRoomResponse

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Gdx.app.log("Http", "Response::createRoom(name=$name, failed), ${e.message!!}")
                val failureResponse = CreateRoomResponse(false, e.message!!, "", "", "", "")
                Gdx.app.postRunnable {
                    action(failureResponse)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Gdx.app.log("Http", "Response::createRoom(name=$name, succeeded)")
                    responseObject = Gson().fromJson(response.body?.string(), CreateRoomResponse::class.java)
                    Gdx.app.postRunnable {
                        action(responseObject)
                    }
                }
            }
        })
    }

    /**
     * Attempts to start a game in a room
     *
     * @param roomId id of the room where the game is supposed to start
     * @param playerId id of the player who wants to start a game
     * @param action a function that will execute when http response is received
     */
    fun startGame(roomId: String, playerId: String, action: (SimpleResponse) -> Unit) {
        Gdx.app.log("Http", "Request::startGame(roomId=$roomId)")
        simpleRequest(roomId, playerId, "start", action)
    }

    /**
     * Attempts to end a game in a room
     *
     * @param roomId id of the room where the game is supposed to end
     * @param playerId id of the player who wants to end a game
     * @param action a function that will execute when http response is received
     */
    fun endGame(roomId: String, playerId: String, action: (SimpleResponse) -> Unit) {
        Gdx.app.log("Http", "Request::endGame(roomId=$roomId)")
        simpleRequest(roomId, playerId, "end", action)
    }

    /**
     * Player Attempts to leave the room a room
     *
     * @param roomId id of the room which player want to leave
     * @param playerId id of the player who wants to leave a room
     * @param action a function that will execute when http response is received
     */
    fun leaveRoom(roomId: String, playerId: String, action: (SimpleResponse) -> Unit) {
        Gdx.app.log("Http", "Request::leaveRoom(roomId=$roomId)")
        simpleRequest(roomId, playerId, "leave", action)
    }

    /**
     * Attempts to remove a room
     *
     * @param roomId id of the room that is supposed to be deleted
     * @param playerId id of the player who wants to delete a room
     * @param action a function that will execute when http response is received
     */
    fun removeRoom(roomId: String, playerId: String, action: (SimpleResponse) -> Unit) {
        Gdx.app.log("Http", "Request::removeRoom(roomId=$roomId)")
        simpleRequest(roomId, playerId, "remove", action)
    }

    private fun simpleRequest(roomId: String, playerId: String, path: String, action: (SimpleResponse) -> Unit) {
        val requestBody: RequestBody = FormBody.Builder()
                .add("roomId", roomId)
                .add("playerId", playerId)
                .build()

        val request: Request = Request.Builder()
                .url("$API_URL/$path")
                .post(requestBody)
                .build()

        var responseObject: SimpleResponse

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Gdx.app.log("Http", "Response::${path}Room(roomId=$roomId, failed), ${e.message}")
                val failureResponse = SimpleResponse(false, e.message!!)
                Gdx.app.postRunnable {
                    action(failureResponse)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Gdx.app.log("Http", "Response::${path}Room(roomId=$roomId, succeeded)")
                    responseObject = Gson().fromJson(response.body?.string(), SimpleResponse::class.java)
                    Gdx.app.postRunnable {
                        action(responseObject)
                    }
                }
            }
        })
    }
}