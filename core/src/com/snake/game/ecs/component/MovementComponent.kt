package com.snake.game.ecs.component

class MovementComponent(val maxSpeed: Float) : Component(ComponentType.Movement) {
    // Velocity
    var vx: Float = 0f;
    var vy: Float = 0f;

    // Acceleration
    var ax: Float = 0f;
    var ay: Float = 0f;

}