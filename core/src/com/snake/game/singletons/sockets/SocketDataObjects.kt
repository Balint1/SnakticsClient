package com.snake.game.singletons.sockets

import org.json.JSONObject

object Data {
    val JOIN_REQUEST: (String, String) -> JSONObject = { nickname: String, roomId: String ->
        JSONObject("""{"nickname":"$nickname", "room_id":"$roomId"}""")
    }
    val SLIDER_CHANGE: (Int) -> JSONObject = { value: Int ->
        JSONObject("""{"value":"$value"}""")
    }
}

object Events {
    const val NEW_PLAYER = "new-player"
    const val JOIN_REQUEST = "join-request"
    const val JOIN_SUCCEEDED = "join-succeeded"
    const val JOIN_FAILED = "join-failed"
    const val UPDATE = "update-state"
    const val SLIDER_CHANGE = "slider-change"
}