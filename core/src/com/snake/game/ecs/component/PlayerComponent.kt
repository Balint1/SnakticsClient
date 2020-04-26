package com.snake.game.ecs.component

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class PlayerComponent() : Component(ComponentType.Player) {
    var playerId: String = ""
    var alive: Boolean = true
    var decaying: Boolean = false
    var remainingDecayTicks: Int = 0
    var invisible: Boolean = false
    var fireballCount = 0
    var throughWallsCount = 0

    override fun updateFromJSON(data: JSONObject) {
        playerId = data.getString("playerId")
        alive = data.getBoolean("alive")
        decaying = data.getBoolean("decaying")
        checkForItemPowerup(data.getJSONArray("powerups"))

        if (decaying)
            remainingDecayTicks = data.getInt("remainingDecayTicks")

        invisible = data.getBoolean("invisible")
    }

    private fun checkForItemPowerup(powerupsJson: JSONArray) {
        fireballCount = 0
        throughWallsCount = 0

        if (powerupsJson.length() > 0) {
            val powerups: Array<PowerupType> = Gson().fromJson(powerupsJson.toString(), Array<PowerupType>::class.java)
            for (powerup: PowerupType in powerups) {
                if (powerup.activationStatus != "used") {
                    if (powerup.type == TagComponent.EntityTagType.Fireball.typeString) {
                        fireballCount ++
                    } else if (powerup.type == TagComponent.EntityTagType.ThroughWalls.typeString) {
                        throughWallsCount ++
                    }
                }
            }
        }
    }

    data class PowerupType(
        var type: String,
        var activationStatus: String
    )
}