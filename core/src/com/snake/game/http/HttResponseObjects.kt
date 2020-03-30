package com.snake.game.http

data class Room(
        var id: String,
        var name: String,
        var hasPassword: Boolean,
        var capacity: Int,
        var players: Int,
        var inProgress: Boolean
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
        var password: String,
        var id: String,
        var ownerId: String
)


data class SimpleResponse(
        var success: Boolean,
        var message: String
)
