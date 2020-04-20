package com.snake.game.ecs.system

import com.snake.game.ecs.component.ComponentNetworkUpdate
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.entity.Entity

/**
 * Updates entity components with values received from the server
 */
object ComponentSyncSystem: System(ComponentType.Position) {
    // Store updates for each entity so we can apply them later
    private var entityUpdates: HashMap<String, MutableList<ComponentNetworkUpdate>> = HashMap()

    override fun update(dt: Float, entity: Entity) {
        // If this entity has pending updates
        if(entityUpdates.containsKey(entity.id)) {
            // Apply all updates
            for(update in entityUpdates[entity.id]!!) {
                if(entity.hasComponent(update.componentType))
                    update.apply(entity.getComponent(update.componentType)!!)
            }
        }
        // Clear updates
        entityUpdates.clear()
    }

    /**
     * Adds an update to one component of a given entity, that will be applied on the next system update.
     * @param entityID The ID of an entity
     * @param update a component update (specifies component type and values)
     */
    fun addEntityUpdate(entityID: String, update: ComponentNetworkUpdate) {
        if(!entityUpdates.containsKey(entityID))
            entityUpdates[entityID] = ArrayList()
        entityUpdates[entityID]!!.add(update)
    }
}