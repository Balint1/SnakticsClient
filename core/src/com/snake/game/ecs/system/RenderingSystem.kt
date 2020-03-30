package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.snake.game.ecs.component.AnimatedSpriteComponent
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.PositionComponent
import com.snake.game.ecs.component.SpriteComponent
import com.snake.game.ecs.entity.Entity

/**
 * Handles rendering entities that have a position and a sprite component.
 */
object RenderingSystem: System(ComponentType.Position and (ComponentType.AnimatedSprite or ComponentType.Sprite)) {
    private val spriteBatch = SpriteBatch()

    override fun update(dt: Float, entity: Entity) {
        val pos = entity.getComponent(ComponentType.Position) as PositionComponent

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        spriteBatch.begin()

        if(entity.hasComponent(ComponentType.Sprite))
            render(entity.getComponent(ComponentType.Sprite) as SpriteComponent, pos.x, pos.y)
        if(entity.hasComponent(ComponentType.AnimatedSprite))
            render(entity.getComponent(ComponentType.AnimatedSprite) as AnimatedSpriteComponent, pos.x, pos.y)

        spriteBatch.end()
    }

    private fun render(spriteComponent: SpriteComponent, x: Float, y: Float) {
        spriteBatch.draw(spriteComponent.sprite, x, y, spriteComponent.renderWidth, spriteComponent.renderHeight)
    }

    private fun render(animComponent: AnimatedSpriteComponent, x: Float, y: Float) {
        // Draw the current frame of this animation
        spriteBatch.draw(animComponent.getCurrentFrame(), x, y, animComponent.renderWidth, animComponent.renderHeight)
    }
}