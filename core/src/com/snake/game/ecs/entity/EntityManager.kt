package com.snake.game.ecs.entity

import com.badlogic.gdx.Gdx
import com.snake.game.ecs.component.Component
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.ComponentTypeOperator
import com.snake.game.ecs.component.ComponentTypeTree
import java.util.EnumMap

/**
 * Manages entities in our ECS architecture.
 * @constructor Creates an Entity Manager with no entities.
 */
class EntityManager {
    // Store entities by ID
    private var entities: HashMap<String, Entity> = HashMap()

    // Map each component type to a list of entities that have that component
    private var entityMap: EnumMap<ComponentType, ArrayList<Entity>> = EnumMap(ComponentType::class.java)

    // Store entities to add in a cache so we can add them on the next update
    private var newEntityCache: HashMap<String, Entity> = HashMap()

    // Store the ID of entities to remove in a cache so we can remove them on the next update
    private var removeEntityCache: ArrayList<String> = ArrayList()

    init {
        // For each component type, create an empty set that will store references to all entities with that component type
        for (componentType in ComponentType.values())
            entityMap[componentType] = ArrayList()
    }

    fun clearEntities() {
        entities.clear()
        newEntityCache.clear()
        removeEntityCache.clear()
    }

    fun updateEntityStorage() {
        // Remove entities that were scheduled to be deleted
        removeEntityCache.forEach { id -> removeEntity(id) }
        removeEntityCache.clear()

        // Add entities that were scheduled to be added
        newEntityCache.values.forEach { entity -> addEntity(entity) }
        newEntityCache.clear()
    }

    /**
     * Get whether or not the entity manager has an entity with the given ID.
     * @param entityId an entity UUID.
     * @return true if an entity with the given id exists, else false.
     */
    fun hasEntity(entityId: String): Boolean {
        return entities.containsKey(entityId) || newEntityCache.containsKey(entityId)
    }

    /**
     * Get an entity by its ID.
     * @param entityId an entity UUID.
     * @return the entity if it exists, else null.
     */
    fun getEntity(entityId: String): Entity? {
        return when {
            entities.containsKey(entityId) -> entities[entityId]
            newEntityCache.containsKey(entityId) -> newEntityCache[entityId]
            else -> null
        }
    }

    internal fun mapEntityComponent(componentType: ComponentType, entity: Entity) {
        // This check prevents mapping components for entities that are in newEntityCache but not added yet
        // (avoids concurrent modification exceptions)
        if(entities.containsKey(entity.id))
            entityMap[componentType]!!.add(entity)
    }

    /**
     * Get all entities that match the component type tree.
     * @param componentTree a component type tree (i.e. (Position and Movement) or (Collision and Position and Movement))
     * @return a list of entities that match the given [componentTree]
     */
    fun getEntities(componentTree: ComponentTypeTree): List<Entity> {
        return when {
            // Leaf node
            componentTree.leftChild == null -> entityMap[componentTree.node.type]!!
            // AND
            componentTree.node.operator == ComponentTypeOperator.AND ->
                getEntities(componentTree.leftChild!!).intersect(getEntities(componentTree.rightChild!!)).toList()
            // OR
            componentTree.node.operator == ComponentTypeOperator.OR ->
                getEntities(componentTree.leftChild!!).union(getEntities(componentTree.rightChild!!)).toList()
            // Should never happen
            else -> ArrayList()
        }
    }

    /**
     * Schedules an entity to be added on the next update.
     * @param entity the entity to add.
     */
    fun scheduleAddEntity(entity: Entity) {
        newEntityCache[entity.id] = entity
    }

    /**
     * Adds an entity to storage.
     * @param entity the entity to add.
     */
    private fun addEntity(entity: Entity) {
        entities[entity.id] = entity
        for (c in entity.getComponents())
            mapEntityComponent(c.type, entity)
    }

    /**
     * Schedules an entity to be deleted on the next update.
     * @param entityId the ID of the entity to remove.
     */
    fun scheduleRemoveEntity(entityId: String) {
        removeEntityCache.add(entityId)
    }

    /**
     * Removes an entity by ID.
     * @param entityId the ID of an entity.
     */
    private fun removeEntity(entityId: String) {
        if (hasEntity(entityId)) {
            for (component in getEntity(entityId)!!.getComponents()) {
                val toRemove = entityMap[component.type]!!.filter { e -> e.id == entityId }
                for (entity in toRemove)
                    entityMap[component.type]!!.remove(entity)
            }
            entities.remove(entityId)
        }
    }
}