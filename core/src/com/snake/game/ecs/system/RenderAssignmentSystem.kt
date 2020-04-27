package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.snake.game.RenderingConstants
import com.snake.game.ecs.component.*

import com.snake.game.ecs.entity.Entity
import org.omg.IOP.TAG_MULTIPLE_COMPONENTS

/**
 * Adds sprite and other rendering components to entities when required.
 */
object RenderAssignmentSystem : System(ComponentType.PowerUp or ComponentType.Tag) {
    override fun update(dt: Float, entity: Entity) {
        val hasSprite = entity.hasComponent(ComponentType.Sprite)
        // Add sprites to power-ups
        if (!hasSprite && entity.hasComponent(ComponentType.PowerUp)) {
            val powerUpComponent = entity.getComponent(ComponentType.PowerUp) as PowerUpComponent
            val ptype = powerUpComponent.powerUpType

            if (ptype?.spriteFile != null) {
                val spriteComponent = SpriteComponent(ptype.spriteFile, ptype.spriteSize, ptype.spriteSize)
                entity.addComponent(spriteComponent)
                entity.addComponent(BouncingRenderComponent(RenderingConstants.POWERUP_BOUNCE_AMPLITUDE, RenderingConstants.POWERUP_BOUNCE_DURATION))
                entity.addComponent(ShadowRenderComponent(ptype.spriteSize, ptype.spriteSize / 3, -ptype.spriteSize + 5f))
            }
        }

        if(entity.hasComponent(ComponentType.Tag)) {
            val tag = (entity.getComponent(ComponentType.Tag) as TagComponent).tag

            // Add fireball sprite
            if(!hasSprite && tag == TagComponent.EntityTagType.Fireball) {
                val spriteComponent = SpriteComponent("powerup-sprites/fireball.png", 20f, 20f)
                entity.addComponent(spriteComponent)
            }

            // Add food sprite
            if(!hasSprite && tag == TagComponent.EntityTagType.Food) {
                var s = 16f
                val spriteComponent = SpriteComponent("powerup-sprites/food.png", s, s)
                entity.addComponent(spriteComponent)
                entity.addComponent(BouncingRenderComponent(RenderingConstants.POWERUP_BOUNCE_AMPLITUDE, RenderingConstants.POWERUP_BOUNCE_DURATION))
                entity.addComponent(ShadowRenderComponent(s, s / 3, -s + 5))
            }
        }
      }
}