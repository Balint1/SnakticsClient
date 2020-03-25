package com.snake.game.singletons.http

import com.google.gson.Gson
import khttp.get
import khttp.post

class HttpService {

    /**
     * Gets a list of all available rooms from the server using http request
     * @return list ot rooms
     */
    fun getRooms(): GetRoomsResponse {
        val responseString: String = get("http://localhost:5000/api/rooms").jsonObject.toString()
        return Gson().fromJson(responseString, GetRoomsResponse::class.java)
    }

    /**
     * Attempts to create a new room
     * @param name name of the room
     * @param capacity maximum allowed players in room
     * @param ownerId id of player who is trying to created the room
     * @return server response regarding room creation
     */
    fun createRoom(name: String, capacity: Int, ownerId: String): CreateRoomResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/create",
                data = mapOf("name" to name, "capacity" to capacity, "ownerId" to ownerId)
        ).jsonObject.toString()
        return Gson().fromJson(responseString, CreateRoomResponse::class.java)
    }

    fun startGame(roomId: String, playerId: String): StartGameResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/start",
                data = mapOf("roomId" to roomId, "playerId" to playerId)
        ).jsonObject.toString()
        return Gson().fromJson(responseString, StartGameResponse::class.java)
    }

    fun endGame(roomId: String, playerId: String): EndGameResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/endgame",
                data = mapOf("roomId" to roomId, "playerId" to playerId)
                ).jsonObject.toString()
        return Gson().fromJson(responseString, EndGameResponse::class.java)
    }

    fun removeRoom(roomId: String, playerId: String): RemoveRoomResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/remove",
                data = mapOf("roomId" to roomId, "playerId" to playerId)
        ).jsonObject.toString()
        return Gson().fromJson(responseString, RemoveRoomResponse::class.java)
    }
}