package com.snake.game.singletons.sockets

import org.json.JSONObject

object Data {
    val JOIN_REQUEST: (String, String, String) -> JSONObject =
            { nickname: String, roomId: String, password: String ->
                JSONObject("""{"nickname":"$nickname", "room_id":"$roomId", "password":"$password"}""")
            }
    val SLIDER_CHANGE: (Int) -> JSONObject = { value: Int ->
        JSONObject("""{"value":"$value"}""")
    }
}

data class JoinResponse(
        var success: Boolean,
        var isOwner: Boolean,
        var id: String,
        var message: String
)

object Events {
    const val NEW_PLAYER = "new-player"
    const val JOIN_REQUEST = "join-request"
    const val JOIN_RESPONSE = "join-response"
    const val UPDATE = "update-state"
    const val SLIDER_CHANGE = "slider-change"
}