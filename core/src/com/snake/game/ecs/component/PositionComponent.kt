package com.snake.game.ecs.component

class PositionComponent(val x: Float, val y: Float) : Component(ComponentType.Position) {
    constructor() : this(0f, 0f)
}