package com.snake.game.ecs

import com.snake.game.ecs.system.AnimationSystem
import com.snake.game.ecs.system.ComponentSyncSystem
import com.snake.game.ecs.system.RenderingSystem
import com.snake.game.ecs.system.RenderAssignmentSystem

object SnakeECSEngine : ECSEngine(
        ComponentSyncSystem, AnimationSystem, RenderingSystem, RenderAssignmentSystem) {

    var localPlayerId: String? = null

    init {

    }

    fun createEntities() {
        entityManager.clearEntities()
    }
}