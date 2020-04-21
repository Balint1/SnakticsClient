package com.snake.game.ecs

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.snake.game.ecs.entity.EntityManager
import com.snake.game.ecs.system.System

/**
 * Singleton class that handles the Entity Component System
 */
open class ECSEngine(vararg systems: System) {
    val entityManager: EntityManager = EntityManager()
    private var systems: List<System> = systems.asList()

    /**
     * Update the entities.
     * @param dt the elapsed time since the last update, in ms.
     */
    fun update(dt: Float) {
        for (system in systems) {
            // Update all entities that match the component tree for this system
            for (entity in entityManager.getEntities(system.componentTypes))
                system.update(dt, entity)
        }
    }

    /**
     * Render the entities.
     * @param sb a sprite batch
     * @param width the world width
     * @param height the world height
     */
    fun render(sb: SpriteBatch, width: Float, height: Float) {
        for (system in systems)
            system.render(sb, entityManager, width, height)
    }
}