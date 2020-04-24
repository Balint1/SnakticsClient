package com.snake.game.ecs.component

enum class ComponentType(val internalName: String) {
    Tag("tag"),
    Player("player"),
    Position("position"),
    Movement("movement"),
    Snake("snake"),
    Sprite("sprite"),
    AnimatedSprite("animatedsprite");

    infix fun and(component: ComponentType): ComponentTypeTree {
        return ComponentTypeTree(this) and component
    }
    infix fun or(component: ComponentType): ComponentTypeTree {
        return ComponentTypeTree(this) or component
    }
    infix fun and(componentTree: ComponentTypeTree): ComponentTypeTree {
        return componentTree and this
    }
    infix fun or(componentTree: ComponentTypeTree): ComponentTypeTree {
        return componentTree or this
    }
}

/**
 * Get a component type from its internal name.
 * @param internalName a string representing a component type (e.g. "position", "movement", etc).
 * @return the associated component type, if it exists (else null).
 */
fun componentTypeFromInternalName(internalName: String): ComponentType? {
    for (type in ComponentType.values()) {
        if (type.internalName == internalName)
            return type
    }
    return null
}

/**
 * Get a new component for the given component type.
 * @param type a component type.
 * @return a component of the given type
 */
fun createComponent(type: ComponentType): Component {
    return when (type) {
        ComponentType.Position -> PositionComponent()
        ComponentType.Movement -> MovementComponent()
        ComponentType.Tag -> TagComponent()
        ComponentType.Player -> PlayerComponent()
        ComponentType.Snake -> SnakeComponent()
        ComponentType.Sprite -> TODO()
        ComponentType.AnimatedSprite -> TODO()
    }
}