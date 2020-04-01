package com.snake.game.ecs.system

import com.snake.game.ecs.component.AnimatedSpriteComponent
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.entity.Entity

object AnimationSystem : System(ComponentType.AnimatedSprite) {
    override fun update(dt: Float, entity: Entity) {
        var animComponent: AnimatedSpriteComponent = entity.getComponent(ComponentType.AnimatedSprite) as AnimatedSpriteComponent
        animComponent.time += dt
    }
}