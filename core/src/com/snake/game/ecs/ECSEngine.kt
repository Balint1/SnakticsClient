package com.snake.game.ecs

import com.badlogic.gdx.Gdx
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
            Gdx.app.debug("test", "updating a system (" + entityManager.getEntities(system.requiredComponents).size + " entities)")
            // Update all entities that have the required components for the current system
            for (entity in entityManager.getEntities(system.requiredComponents))
                system.update(dt, entity)
        }
    }
}