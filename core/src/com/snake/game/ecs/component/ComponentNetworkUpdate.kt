package com.snake.game.ecs.component

/**
 * Stores a component update from the network that has not been processed yet.
 */
class ComponentNetworkUpdate(val componentType: ComponentType, private val values: Map<String, String>) {
    /**
     * Apply this update on a given component.
     * @param component A component whose type must match this ComponentNetworkUpdate's componentType
     */
    fun apply(component: Component) {
        if (componentType == ComponentType.Position) {
            val pos = component as PositionComponent
            if (values.containsKey("x")) {
                val value = if (values["x"] == null) null else values.getValue("x").toFloatOrNull()
                if (value != null)
                    pos.x = value
            }
            if (values.containsKey("y")) {
                val value = if (values["y"] == null) null else values.getValue("y").toFloatOrNull()
                if (value != null)
                    pos.y = value
            }
        }
    }
}