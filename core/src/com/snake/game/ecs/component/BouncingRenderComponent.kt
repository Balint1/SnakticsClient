package com.snake.game.ecs.component

class BouncingRenderComponent(
    val amplitude: Float,
    val duration: Float
) : Component(ComponentType.BouncingRender) {
    // Current time in the animation
    var time: Float = 0f
    var offsetY: Float = 0f
}