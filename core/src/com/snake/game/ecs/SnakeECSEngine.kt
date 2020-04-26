package com.snake.game.ecs

import com.snake.game.ecs.system.AnimationSystem
import com.snake.game.ecs.system.ComponentSyncSystem
import com.snake.game.ecs.system.RenderingSystem
import com.snake.game.ecs.system.SpriteAssignmentSystem

object SnakeECSEngine : ECSEngine(
        ComponentSyncSystem, AnimationSystem, RenderingSystem, SpriteAssignmentSystem) {

    var localPlayerId: String? = null

    init {
    }

    fun createEntities() {
        entityManager.clearEntities()
    }
}