package com.snake.game.backend

import org.json.JSONObject

object Data {
    val JOIN_REQUEST: (String, String, String) -> JSONObject =
            { nickname: String, roomId: String, password: String ->
                JSONObject("""{"nickname":"$nickname", "room_id":"$roomId", "password":"$password"}""")
            }

    val SWIPE: (String) -> JSONObject = { swipeDirection: String->
        JSONObject("""{"direction":"$swipeDirection"}""")
    }
}

enum class Events(val value: String) {
    NEW_PLAYER("new-player"),
    PLAYER_LEFT("player-left"),
    JOIN_REQUEST("join-request"),
    JOIN_RESPONSE("join-response"),
    UPDATE("update-state"),
    SWIPE("swipe"),
    OWNER_CHANGED("owner-changed"),
    LEAVE_RESPONSE("leave-response"),
    START_GAME("start-game")
}