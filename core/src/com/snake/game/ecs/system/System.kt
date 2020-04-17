package com.snake.game.ecs.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.ComponentTypeTree
import com.snake.game.ecs.entity.Entity

abstract class System(var componentTypes: ComponentTypeTree) {
    constructor(componentType: ComponentType) : this(ComponentTypeTree(componentType))

    /**
     * Update the given entity.
     * @param dt the elapsed time since the last update, in ms.
     * @param entity an entity that matches this system's component tree.
     */
    abstract fun update(dt: Float, entity: Entity)

    /**
     * Render function that can be overridden to add rendering capabilities to the system.
     */
    fun render(sb: SpriteBatch) {}
}