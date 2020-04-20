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
    val JOYSTICK_CHANGE: (Float) -> JSONObject = { x: Float->
        JSONObject("""{"value":"$x"}""")
    }
}

enum class Events(val value: String) {
    NEW_PLAYER("new-player"),
    PLAYER_LEFT("player-left"),
    JOIN_REQUEST("join-request"),
    JOIN_RESPONSE("join-response"),
    UPDATE("update-state"),
    SLIDER_CHANGE("slider-change"),
    JOYSTICK_CHANGE("joystick-change"),
    OWNER_CHANGED("owner-changed"),
    LEAVE_RESPONSE("leave-response"),
    START_GAME("start-game")
}