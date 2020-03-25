package com.snake.game.singletons.http

data class Room(
        var id: String,
        var capacity: Int,
        var players: Int
)

data class GetRoomsResponse(
        var rooms: MutableList<Room>
)

data class CreateRoomResponse(
        var success: Boolean,
        var message: String,
        var name: String,
        var id: String
)

data class StartGameResponse(
        var succsess: Boolean
)

data class EndGameResponse(
        var succsess: Boolean
)

data class RemoveRoomResponse(
        var succsess: Boolean
)