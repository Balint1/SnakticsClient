package com.snake.game.singletons.http

import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import com.snake.game.Config.API_URL
import com.snake.game.singletons.sockets.SocketService
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
     * Gets a list of all available rooms from the server using http request
     * @param action a function that will execute when http response is received
     */
    fun getRooms(action: (GetRoomsResponse) -> Unit) {
        canceled = false
        val request: Request = Request.Builder().url(API_URL).build()
        var responseObject: GetRoomsResponse
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!canceled) {
                    val failureResponse = GetRoomsResponse(false, e.message!!, mutableListOf())
                    Gdx.app.postRunnable {
                        action(failureResponse)
                    }
                    e.printStackTrace()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!canceled) {
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
        canceled = true
    }

    /**
     * Attempts to create a new room
     * @param name name of the room
     * @param password optional password for the room
     * @param capacity maximum allowed players in room
     * @param action a function that will execute when http response is received
     */
    fun createRoom(name: String, password: String, capacity: Int, action: (CreateRoomResponse) -> Unit) {
        val requestBody: RequestBody = FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("capacity", capacity.toString())
                .add("ownerId", SocketService.socket.id())
                .build()

        val request: Request = Request.Builder()
                .url("$API_URL/create")
                .post(requestBody)
                .build()

        var responseObject: CreateRoomResponse

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val failureResponse = CreateRoomResponse(false, e.message!!, "", "", "", "")
                Gdx.app.postRunnable {
                    action(failureResponse)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
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
     * @param roomId id of the room where the game is supposed to start
     * @param playerId id of the player who wants to start a game
     * @param action a function that will execute when http response is received
     */
    fun startGame(roomId: String, playerId: String, action: (StartGameResponse) -> Unit) {
        val requestBody: RequestBody = FormBody.Builder()
                .add("roomId", roomId)
                .add("playerId", playerId)
                .build()

        val request: Request = Request.Builder()
                .url("$API_URL/start")
                .post(requestBody)
                .build()

        var responseObject: StartGameResponse

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val failureResponse = StartGameResponse(false, e.message!!)
                Gdx.app.postRunnable {
                    action(failureResponse)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    responseObject = Gson().fromJson(response.body?.string(), StartGameResponse::class.java)
                    Gdx.app.postRunnable {
                        action(responseObject)
                    }
                }
            }
        })
    }

    /**
     * Attempts to end a game in a room
     * @param roomId id of the room where the game is supposed to end
     * @param playerId id of the player who wants to end a game
     * @param action a function that will execute when http response is received
     */
    fun endGame(roomId: String, playerId: String, action: (EndGameResponse) -> Unit) {
        val requestBody: RequestBody = FormBody.Builder()
                .add("roomId", roomId)
                .add("playerId", playerId)
                .build()

        val request: Request = Request.Builder()
                .url("$API_URL/end")
                .post(requestBody)
                .build()

        var responseObject: EndGameResponse

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val failureResponse = EndGameResponse(false, e.message!!)
                Gdx.app.postRunnable {
                    action(failureResponse)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    responseObject = Gson().fromJson(response.body?.string(), EndGameResponse::class.java)
                    Gdx.app.postRunnable {
                        action(responseObject)
                    }
                }
            }
        })
    }

    /**
     * Attempts to remove a room
     * @param roomId id of the room that is supposed to be deleted
     * @param playerId id of the player who wants to delete a room
     * @param action a function that will execute when http response is received
     */
    fun removeRoom(roomId: String, playerId: String, action: (RemoveRoomResponse) -> Unit) {
        val requestBody: RequestBody = FormBody.Builder()
                .add("roomId", roomId)
                .add("playerId", playerId)
                .build()

        val request: Request = Request.Builder()
                .url("$API_URL/remove")
                .post(requestBody)
                .build()

        var responseObject: RemoveRoomResponse

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val failureResponse = RemoveRoomResponse(false, e.message!!)
                Gdx.app.postRunnable {
                    action(failureResponse)
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    responseObject = Gson().fromJson(response.body?.string(), RemoveRoomResponse::class.java)
                    Gdx.app.postRunnable {
                        action(responseObject)
                    }
                }
            }
        })
    }
}