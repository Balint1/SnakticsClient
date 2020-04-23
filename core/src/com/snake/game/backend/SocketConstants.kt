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
    val JOYSTICK_CHANGE: (Float) -> JSONObject = { x: Float ->
        JSONObject("""{"value":"$x"}""")
    }
    val SWIPE: (String) -> JSONObject = { swipeDirection: String ->
        JSONObject("""{"direction":"$swipeDirection"}""")
    }
}

enum class Events(val value: String) {
    NEW_PLAYER("new-player"),
    PLAYER_LEFT_ROOM("player-left-room"),
    JOIN_REQUEST("join-request"),
    JOIN_RESPONSE("join-response"),
    UPDATE("update-state"),
    SLIDER_CHANGE("slider-change"),
    JOYSTICK_CHANGE("joystick-change"),
    SWIPE("swipe"),
    OWNER_CHANGED("owner-changed"),
    LEAVE_RESPONSE("leave-response"),
    LEAVE_TO_LOBBY("leave-to-lobby"),
    PLAYER_LEFT_GAME("player-left-game"),
    LEAVE_TO_LOBBY_RESPONSE("leave-to-lobby-response"),
    START_GAME("start-game")
}