package com.snake.game.ecs

import com.snake.game.ecs.system.AnimationSystem
import com.snake.game.ecs.system.ComponentSyncSystem
import com.snake.game.ecs.system.RenderingSystem

object SnakeECSEngine : ECSEngine(
        ComponentSyncSystem, AnimationSystem, RenderingSystem) {
    init {
    }

    fun createEntities() {
        entityManager.clearEntities()
    }
}