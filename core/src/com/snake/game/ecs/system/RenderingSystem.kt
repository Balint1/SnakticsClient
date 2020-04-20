package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.snake.game.ecs.component.*
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityManager

/**
 * Handles rendering entities that have a position and a sprite component.
 */
//object RenderingSystem: System(ComponentType.Position and (ComponentType.AnimatedSprite or ComponentType.Sprite)) {
object RenderingSystem: System(ComponentType.Position) {
    override fun update(dt: Float, entity: Entity) {
        val pos = entity.getComponent(ComponentType.Position) as PositionComponent
    }

    override fun render(sb: SpriteBatch, em: EntityManager) {
        super.render(sb, em)

        val entities = em.getEntities(componentTypes)

        val sr = ShapeRenderer()
        val font = BitmapFont()
        font.color = Color.WHITE


        sr.projectionMatrix = sb.projectionMatrix
        for(i in entities.indices) {
            // Iterate over indices to avoid ConcurrentModificationException
            val entity = entities.elementAt(i)

            val pos = entity.getComponent(ComponentType.Position) as PositionComponent

            if (entity.hasComponent(ComponentType.Sprite))
                render(sb, entity.getComponent(ComponentType.Sprite) as SpriteComponent, pos.x, pos.y)
            if (entity.hasComponent(ComponentType.AnimatedSprite))
                render(sb, entity.getComponent(ComponentType.AnimatedSprite) as AnimatedSpriteComponent, pos.x, pos.y)

            // Temporary render
            sr.begin(ShapeRenderer.ShapeType.Filled)
            sr.color = Color.RED
            sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
            sr.end()

            if(entity.hasComponent(ComponentType.Tag)) {
                Gdx.app.log("Entity tag", (entity.getComponent(ComponentType.Tag) as TagComponent).tag)
                font.draw(sb, (entity.getComponent(ComponentType.Tag) as TagComponent).tag, pos.x, pos.y)
            }

        }
    }

    private fun render(sb: SpriteBatch, spriteComponent: SpriteComponent, x: Float, y: Float) {
        sb.draw(spriteComponent.sprite, x, y, spriteComponent.renderWidth, spriteComponent.renderHeight)
    }

    private fun render(sb: SpriteBatch, animComponent: AnimatedSpriteComponent, x: Float, y: Float) {
        // Draw the current frame of this animation
        sb.draw(animComponent.getCurrentFrame(), x, y, animComponent.renderWidth, animComponent.renderHeight)
    }
}