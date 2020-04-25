package com.snake.game.ecs.entity

import com.badlogic.gdx.Gdx
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.ComponentTypeOperator
import com.snake.game.ecs.component.ComponentTypeTree
import java.util.EnumMap
import kotlin.collections.HashSet

/**
 * Manages entities in our ECS architecture.
 * @constructor Creates an Entity Manager with no entities.
 */
class EntityManager {
    // Store entities by ID
    private var entities: HashMap<String, Entity> = HashMap()

    // Map each component type to a list of entities that have that component
    private var entityMap: EnumMap<ComponentType, ArrayList<Entity>> = EnumMap(ComponentType::class.java)

    init {
        clearEntities()
    }

    fun clearEntities() {
        entities.clear()

        // For each component type, create an empty set that will store references to all entities with that component type
        for (componentType in ComponentType.values())
            entityMap[componentType] = ArrayList()
    }

    /**
     * Get whether or not the entity manager has an entity with the given ID.
     * @param entityId an entity UUID.
     * @return true if an entity with the given id exists, else false.
     */
    fun hasEntity(entityId: String): Boolean {
        return entities.containsKey(entityId)
    }

    /**
     * Get an entity by its ID.
     * @param entityId an entity UUID.
     * @return the entity if it exists, else null.
     */
    fun getEntity(entityId: String): Entity? {
        return entities[entityId]
    }

    internal fun addEntity(entity: Entity) {
        entities[entity.id] = entity
        for (c in entity.getComponents())
            mapEntityComponent(c.type, entity)
    }

    internal fun mapEntityComponent(componentType: ComponentType, entity: Entity) {
        entityMap[componentType]!!.add(entity)
    }

    /**
     * Get all entities that match at least a component in a given list of component types
     * @param componentTypes a list of component types
     * @return a set of entities that have at least one of the components listed in [componentTypes]
     */
    /*fun getEntities(componentTypes: List<ComponentType>): Set<Entity> {
        return when {
            componentTypes.isEmpty() -> HashSet()
            componentTypes.size == 1 -> entityMap[componentTypes[0]]!!
            // Recursion case
            else -> getEntities(componentTypes.subList(0, 1))
                    .union(getEntities(componentTypes.subList(1, componentTypes.size)))
        }
    }*/

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
     * Removes an entity by ID.
     * @param entityId the ID of an entity
     */
    fun removeEntity(entityId: String) {
        if(hasEntity(entityId)) {
            for(component in getEntity(entityId)!!.getComponents()) {
                val toRemove = entityMap[component.type]!!.filter { e -> e.id == entityId }
                for(entity in toRemove)
                    entityMap[component.type]!!.remove(entity)
            }

            entities.remove(entityId)
        }
    }
}