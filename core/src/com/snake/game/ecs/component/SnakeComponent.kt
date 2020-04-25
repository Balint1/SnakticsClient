package com.snake.game.ecs.component

import org.json.JSONObject

class SnakeComponent() : Component(ComponentType.Snake) {
    var nextPieceId: String? = null

    override fun updateFromJSON(data: JSONObject) {
        nextPieceId = data.getString("next_entityId")
        if (nextPieceId == "null")
            nextPieceId = null
    }
}