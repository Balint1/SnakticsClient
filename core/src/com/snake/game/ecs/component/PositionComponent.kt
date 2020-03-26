package com.snake.game.ecs.component

class PositionComponent(var x: Float, var y: Float) : Component(ComponentType.Position) {
    constructor(): this(0f,0f)
}