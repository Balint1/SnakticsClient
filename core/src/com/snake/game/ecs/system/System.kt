package com.snake.game.ecs.system

import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.ComponentTypeTree
import com.snake.game.ecs.entity.Entity

abstract class System(var componentTypes: ComponentTypeTree) {
    constructor(componentType: ComponentType) : this(ComponentTypeTree(componentType))

    abstract fun update(dt: Float, entity: Entity)
}