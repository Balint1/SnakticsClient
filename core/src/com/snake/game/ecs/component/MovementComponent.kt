package com.snake.game.ecs.component

import com.google.gson.JsonObject

class MovementComponent() : Component(ComponentType.Movement) {
    // Velocity
    var vx: Float = 0f
    var vy: Float = 0f

    // Acceleration
    var ax: Float = 0f
    var ay: Float = 0f

}