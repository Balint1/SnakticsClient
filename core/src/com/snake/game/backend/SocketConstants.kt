package com.snake.game.backend

import org.json.JSONObject

object Data {
    val JOIN_REQUEST: (String, String, String) -> JSONObject =
            { nickname: String, roomId: String, password: String ->
                JSONObject("""{"nickname":"${nickname.toLowerCase()}", "room_id":"$roomId", "password":"$password"}""")
            }

    val SWIPE: (String) -> JSONObject = { swipeDirection: String ->
        JSONObject("""{"direction":"$swipeDirection"}""")
    }

    val USE_POWEUP: (String) -> JSONObject = { powerup: String ->
        JSONObject("""{"powerup":"$powerup"}""")
    }
}

enum class Events(val value: String) {
    NEW_PLAYER("new-player"),
    PLAYER_LEFT_ROOM("player-left-room"),
    JOIN_REQUEST("join-request"),
    JOIN_RESPONSE("join-response"),
    UPDATE("update-state"),
    DELETE_ENTITIES("delete-entities"),
    SWIPE("swipe"),
    OWNER_CHANGED("owner-changed"),
    LEAVE_RESPONSE("leave-response"),
    LEAVE_TO_LOBBY("leave-to-lobby"),
    PLAYER_LEFT_GAME("player-left-game"),
    LEAVE_TO_LOBBY_RESPONSE("leave-to-lobby-response"),
    START_GAME("start-game"),
    PLAYER_DIED("player-died"),
    YOU_DIED("you-died"),
    USE_POWERUP("use-powerup")
}