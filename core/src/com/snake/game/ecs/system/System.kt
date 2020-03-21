package com.snake.game.ecs.system

import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.entity.Entity

abstract class System(requiredComponents: List<ComponentType>) {
    val requiredComponents: List<ComponentType> = requiredComponents;

    abstract fun update(dt: Float, entity: Entity)
}