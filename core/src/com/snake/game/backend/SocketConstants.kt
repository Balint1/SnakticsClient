package com.snake.game.backend

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

enum class Events(val value: String) {
    NEW_PLAYER("new-player"),
    PLAYER_LEFT("player-left"),
    JOIN_REQUEST("join-request"),
    JOIN_RESPONSE("join-response"),
    UPDATE("update-state"),
    SLIDER_CHANGE("slider-change"),
    OWNER_CHANGED("owner-changed"),
    LEAVE_RESPONSE("leave-response"),
    START_GAME("start-game")
}