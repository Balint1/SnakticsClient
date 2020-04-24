package com.snake.game.ecs.component

import org.json.JSONObject

class PlayerComponent() : Component(ComponentType.Player) {
    var playerId: String = ""
    var alive: Boolean = true
    var decaying: Boolean = false
    var remainingDecayTicks: Int = 0

    override fun updateFromJSON(data: JSONObject) {
        playerId = data.getString("playerId")
        // powerups = (json array)
        alive = data.getBoolean("alive")
        decaying = data.getBoolean("decaying")

        if(decaying)
            remainingDecayTicks = data.getInt("remainingDecayTicks")
    }
}