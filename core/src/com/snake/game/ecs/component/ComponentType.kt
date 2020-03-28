package com.snake.game.ecs.component

enum class ComponentType {
    Position,
    Movement,
    Sprite,
    AnimatedSprite;

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