package com.snake.game.ecs.component

import org.json.JSONObject

class PositionComponent() : Component(ComponentType.Position) {
    var x: Float = 0f
    var y: Float = 0f

    override fun updateFromJSON(data: JSONObject) {
        val pos = data.getJSONObject("position")
        x = pos.getDouble("x").toFloat()
        y = pos.getDouble("y").toFloat()
    }
}