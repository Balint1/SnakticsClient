package com.snake.game.ecs.system

import com.snake.game.RenderingConstants
import com.snake.game.ecs.component.*

import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.SpriteComponent
import com.snake.game.ecs.component.PowerUpComponent
import com.snake.game.ecs.entity.Entity


/**
 * Adds sprite and other rendering components to entities when required.
 */
object RenderAssignmentSystem : System(ComponentType.PowerUp) {
    override fun update(dt: Float, entity: Entity) {
        // Add sprites to power-ups
        if (entity.hasComponent(ComponentType.PowerUp) && !entity.hasComponent(ComponentType.Sprite)) {
            val powerUpComponent = entity.getComponent(ComponentType.PowerUp) as PowerUpComponent
            val ptype = powerUpComponent.powerUpType

            if (ptype?.spriteFile != null) {
                val spriteComponent = SpriteComponent(ptype.spriteFile, ptype.spriteSize, ptype.spriteSize)
                entity.addComponent(spriteComponent)
                entity.addComponent(BouncingRenderComponent(RenderingConstants.POWERUP_BOUNCE_AMPLITUDE, RenderingConstants.POWERUP_BOUNCE_DURATION))
                entity.addComponent(ShadowRenderComponent(ptype.spriteSize, ptype.spriteSize/3))
            }
        }
    }
}