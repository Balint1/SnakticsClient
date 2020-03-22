package com.snake.game.sockets

import org.json.JSONObject

object Data {
    fun JOIN_REQUEST(nickname: String, room_id: String): JSONObject {
        return JSONObject("""{"nickname":"$nickname", "room_id":"$room_id"}""")
    }
    fun SLIDER_CHANGE(value: Int): JSONObject{
        return JSONObject("""{"value":"$value"}""")
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