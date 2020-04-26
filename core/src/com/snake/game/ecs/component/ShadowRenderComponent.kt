package com.snake.game.ecs.component

class ShadowRenderComponent(
    val width: Float,
    val height: Float,
    val offsetY: Float
) : Component(ComponentType.ShadowRender) {
    constructor(width: Float, height: Float) : this(width, height, 2f)
}