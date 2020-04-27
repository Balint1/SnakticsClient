package com.snake.game.ecs

import com.snake.game.ecs.system.AnimationSystem
import com.snake.game.ecs.system.RenderingSystem
import com.snake.game.ecs.system.RenderAssignmentSystem

object SnakeECSEngine : ECSEngine(
        AnimationSystem, RenderingSystem, RenderAssignmentSystem) {

    var localPlayerId: String? = null

    init {
    }
}