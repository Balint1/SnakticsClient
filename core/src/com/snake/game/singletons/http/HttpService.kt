package com.snake.game.singletons.http

import khttp.*

class HttpService {
    fun getRooms() {
        println(get("http://localhost:5000/api/rooms").jsonObject)
    }
}