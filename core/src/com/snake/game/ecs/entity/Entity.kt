package com.snake.game.ecs.entity

import com.snake.game.ecs.component.Component
import com.snake.game.ecs.component.ComponentType
import java.util.EnumMap

/**
 * Represents an Entity in our ECS architecture.
 *
 * @constructor Creates an Entity with no components.
 */
class Entity(private val entityManager: EntityManager) {
    private var componentMap: EnumMap<ComponentType, Component> = EnumMap(ComponentType::class.java)

    init {
        entityManager.addEntity(this)
    }

    /**
     * Adds a [component] to this Entity.
     * @return whether or not the component was successfully added (can be false, e.g. if the Entity
     * already has a Component of the same type).
     */
    fun addComponent(component: Component): Boolean {
        if (componentMap.containsKey(component.type))
            return false
        this.componentMap[component.type] = component

        // Notify the EntityManager that a component was added to this entity, so it can update its internal map
        entityManager.mapEntityComponent(component.type, this)
        return true
    }

    /**
     * Get whether of not this entity has a component of the given [type].
     * @return true if this entity has a component of type [type], else false.
     */
    fun hasComponent(type: ComponentType): Boolean {
        return this.componentMap.containsKey(type)
    }

    /**
     * Get all components of this entity.
     * @return this entity's components
     */
    fun getComponents(): Set<Component> {
        return HashSet<Component>(this.componentMap.values)
    }

    /**
     * Get the component of the given [type] for this entity.
     * @return a component of given [type] if this entity has one, else null.
     */
    fun getComponent(type: ComponentType): Component? {
        return this.componentMap[type]
    }
}