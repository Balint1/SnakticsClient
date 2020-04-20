package com.snake.game.ecs.component

import org.json.JSONObject

class PlayerComponent(): Component(ComponentType.Player) {
    var playerId: String = ""

    override fun updateFromJSON(data: JSONObject) {
        playerId = data.getString("playerID")
        // powerups = (json array)
    }
}