package com.snake.game.ecs.component

import org.json.JSONObject

class PowerUpComponent() : Component(ComponentType.PowerUp) {
    var powerUpType: PowerUpType? = null

    override fun updateFromJSON(data: JSONObject) {
        powerUpType = PowerUpType.fromName(data.getString("powerup"))
    }
}