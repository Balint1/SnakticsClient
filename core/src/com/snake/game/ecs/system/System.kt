package com.snake.game.ecs.system

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.ComponentTypeTree
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityManager

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
     * @param sb a sprite batch
     * @param em the entity manager (could pass the entities instead the same way we do in update(), but we want freedom on rendering order)
     */
    open fun render(sb: SpriteBatch, em: EntityManager) {}
}