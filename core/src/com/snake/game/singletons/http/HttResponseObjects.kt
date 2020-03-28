package com.snake.game.singletons.http

data class Room(
        var id: String,
        var capacity: Int,
        var players: Int
)

data class GetRoomsResponse(
        var success: Boolean,
        var message: String,
        var rooms: MutableList<Room>
)

data class CreateRoomResponse(
        var success: Boolean,
        var message: String,
        var name: String,
        var id: String,
        var ownerId: String
)

data class StartGameResponse(
        var success: Boolean,
        var message: String
)

data class EndGameResponse(
        var success: Boolean,
        var message: String
)

data class RemoveRoomResponse(
        var success: Boolean,
        var message: String
)