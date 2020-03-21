package com.snake.game.ecs.entity

import com.snake.game.ecs.component.ComponentType
import java.util.*
import kotlin.collections.HashSet

/**
 * Manages entities in our ECS architecture.
 * @constructor Creates an Entity Manager with no entities.
 */
class EntityManager {
    private var entities: HashSet<Entity> = HashSet()
    private var entityMap: EnumMap<ComponentType, HashSet<Entity>> = EnumMap(ComponentType::class.java)

    init {
        // For each component type, create an empty set that will store references to all entities with that component type
        for(componentType in ComponentType.values())
            entityMap[componentType] = HashSet()
    }

    internal fun addEntity(entity: Entity) {
        entities.add(entity)
        for(c in entity.getComponents())
            mapEntityComponent(c.type, entity)
    }

    internal fun mapEntityComponent(componentType: ComponentType, entity: Entity) {
        entityMap[componentType]!!.add(entity)
    }

    /**
     * Get all entities that match a given list of component types
     * @param componentTypes a list of component types
     * @return a set of entities that have all of the components listed in [componentTypes]
     */
    fun getEntities(componentTypes: List<ComponentType>): Set<Entity> {
        return when {
            componentTypes.isEmpty() -> HashSet()
            componentTypes.size == 1 -> entityMap[componentTypes[0]]!!
            // Recursion case
            else -> getEntities(componentTypes.subList(0, 1))
                    .intersect(getEntities(componentTypes.subList(1, componentTypes.size)))
        }
    }


}