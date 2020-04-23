package com.snake.game.ecs.component

import org.json.JSONObject

/**
 * Represents a Component in our ECS architecture.
 *
 * @property type the type of this component.
 * @constructor Creates a Component of the given [type].
 */
open class Component(val type: ComponentType) {
    open fun updateFromJSON(data: JSONObject) {}
}