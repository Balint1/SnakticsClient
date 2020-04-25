package com.snake.game.ecs.component

import org.json.JSONObject

class SnakeComponent() : Component(ComponentType.Snake) {
    var nextPieceId: String? = null
    var invisible: Boolean = false

    override fun updateFromJSON(data: JSONObject) {
        nextPieceId = data.getString("next_entityId")
        if(nextPieceId == "null")
            nextPieceId = null
        invisible = data.getString("invisible").toBoolean()
    }
}