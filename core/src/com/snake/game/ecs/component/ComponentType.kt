package com.snake.game.ecs.component

enum class ComponentType(val internalName: String) {
    Tag("tag"),
    Player("player"),
    Position("position"),
    Movement("movement"),
    Snake("snake"),
    Sprite("sprite"),
    AnimatedSprite("animatedsprite"),
    PowerUp("powerup");

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

    companion object {
        /**
         * Get a component type from its internal name.
         * @param name a string representing a component type (e.g. "position", "movement", etc).
         * @return the associated component type, if it exists (else null).
         */
        fun fromName(name: String): ComponentType? {
            for (type in ComponentType.values()) {
                if (type.internalName == name)
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
                Position -> PositionComponent()
                Movement -> MovementComponent()
                Tag -> TagComponent()
                Player -> PlayerComponent()
                Snake -> SnakeComponent()
                PowerUp -> PowerUpComponent()
                Sprite -> TODO()
                AnimatedSprite -> TODO()
            }
        }
    }
}