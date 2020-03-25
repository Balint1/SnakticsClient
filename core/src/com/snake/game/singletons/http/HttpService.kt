package com.snake.game.singletons.http

import com.google.gson.Gson
import com.snake.game.singletons.sockets.SocketService
import khttp.get
import khttp.post

object HttpService {

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
     * @return server response regarding room creation
     */
    fun createRoom(name: String, capacity: Int): CreateRoomResponse {
        val id: String? = SocketService.getId()
        return if(id != null){
            val responseString: String = post(
                    url = "http://localhost:5000/api/rooms/create",
                    data = mapOf("name" to name, "capacity" to capacity, "ownerId" to id)
            ).jsonObject.toString()
            Gson().fromJson(responseString, CreateRoomResponse::class.java)
        }else{
            CreateRoomResponse(false, "Socket connection failed", "", "")
        }

    }

    /**
     * Attempts to start a game in a room
     * @param roomId id of the room where the game is supposed to start
     * @param playerId id of the player who wants to start a game
     * @return server response regarding game start
     */
    fun startGame(roomId: String, playerId: String): StartGameResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/start",
                data = mapOf("roomId" to roomId, "playerId" to playerId)
        ).jsonObject.toString()
        return Gson().fromJson(responseString, StartGameResponse::class.java)
    }

    /**
     * Attempts to end a game in a room
     * @param roomId id of the room where the game is supposed to end
     * @param playerId id of the player who wants to end a game
     * @return server response regarding end game
     */
    fun endGame(roomId: String, playerId: String): EndGameResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/endgame",
                data = mapOf("roomId" to roomId, "playerId" to playerId)
                ).jsonObject.toString()
        return Gson().fromJson(responseString, EndGameResponse::class.java)
    }

    /**
     * Attempts to remove a room
     * @param roomId id of the room that is supposed to be deleted
     * @param playerId id of the player who wants to delete a room
     * @return server response regarding delete room
     */
    fun removeRoom(roomId: String, playerId: String): RemoveRoomResponse {
        val responseString: String = post(
                url = "http://localhost:5000/api/rooms/remove",
                data = mapOf("roomId" to roomId, "playerId" to playerId)
        ).jsonObject.toString()
        return Gson().fromJson(responseString, RemoveRoomResponse::class.java)
    }
}