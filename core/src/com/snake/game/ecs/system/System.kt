package com.snake.game.ecs.system

import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.entity.Entity

abstract class System(val requiredComponents: List<ComponentType>) {

    abstract fun update(dt: Float, entity: Entity)
}