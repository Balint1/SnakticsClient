package com.snake.game.sockets

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
        var roomId: String,
        var message: String
)

data class OwnerChangeResponse(
        var success: Boolean,
        var roomId: String
)

enum class Events(val value: String) {
    NEW_PLAYER("new-player"),
    JOIN_REQUEST("join-request"),
    JOIN_RESPONSE("join-response"),
    UPDATE("update-state"),
    SLIDER_CHANGE("slider-change"),
    OWNER_CHANGED("owner-changed")
}