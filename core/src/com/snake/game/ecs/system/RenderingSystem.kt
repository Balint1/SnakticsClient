package com.snake.game.ecs.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.PositionComponent
import com.snake.game.ecs.component.SpriteComponent
import com.snake.game.ecs.component.AnimatedSpriteComponent
import com.snake.game.ecs.component.TagComponent
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityManager

/**
 * Handles rendering entities that have a position and a sprite component.
 */
// object RenderingSystem: System(ComponentType.Position and (ComponentType.AnimatedSprite or ComponentType.Sprite)) {
object RenderingSystem : System(ComponentType.Position) {
    // Rendering constants // TODO move to other file?
    private const val SNAKE_RADIUS = 4f
    private val BACKGROUND_COLOR = Color.DARK_GRAY

    private val debugFont = BitmapFont()

    init {
        debugFont.data.setScale(3f)
        debugFont.color = Color.WHITE
    }

    override fun update(dt: Float, entity: Entity) {
        // val pos = entity.getComponent(ComponentType.Position) as PositionComponent
    }

    override fun render(sb: SpriteBatch, em: EntityManager, worldWidth: Float, worldHeight: Float) {
        super.render(sb, em, worldWidth, worldHeight)

        val entities = em.getEntities(componentTypes)

        val sr = ShapeRenderer()

        sr.projectionMatrix = sb.projectionMatrix
        sr.begin(ShapeRenderer.ShapeType.Filled)

        // Background
        sr.color = BACKGROUND_COLOR
        sr.rect(0f, 0f, worldWidth, worldHeight)

        // Iterate over indices to avoid ConcurrentModificationException
        for (i in 0 until entities.size) {
            val entity = entities.elementAt(i)

            val pos = entity.getComponent(ComponentType.Position) as PositionComponent

            if (entity.hasComponent(ComponentType.Sprite))
                render(sb, entity.getComponent(ComponentType.Sprite) as SpriteComponent, pos.x, pos.y)
            if (entity.hasComponent(ComponentType.AnimatedSprite))
                render(sb, entity.getComponent(ComponentType.AnimatedSprite) as AnimatedSpriteComponent, pos.x, pos.y)

            if (entity.hasComponent(ComponentType.Tag)) {
                val tagComponent = (entity.getComponent(ComponentType.Tag) as TagComponent).tag!!

                // Render snake head
                if (tagComponent == TagComponent.EntityTagType.SnakeHead) {
                    sr.color = Color.FOREST
                    sr.circle(pos.x, pos.y, SNAKE_RADIUS * 1.5f)
                }
                // Render snake body piece
                else if (tagComponent == TagComponent.EntityTagType.SnakeBody) {
                    sr.color = Color.FOREST
                    sr.circle(pos.x, pos.y, SNAKE_RADIUS)
                } else {
                    // Temporary render
                    sr.color = Color.BLUE
                    sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)

                    debugFont.draw(sb, "TEST STRING", 0f, 0f)
                    debugFont.draw(sb, tagComponent.typeString, pos.x, pos.y)
                }
            } else {
                // Temporary render
                sr.color = Color.RED
                sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
            }
        }

        sr.end()
    }

    private fun render(sb: SpriteBatch, spriteComponent: SpriteComponent, x: Float, y: Float) {
        sb.draw(spriteComponent.sprite, x, y, spriteComponent.renderWidth, spriteComponent.renderHeight)
    }

    private fun render(sb: SpriteBatch, animComponent: AnimatedSpriteComponent, x: Float, y: Float) {
        // Draw the current frame of this animation
        sb.draw(animComponent.getCurrentFrame(), x, y, animComponent.renderWidth, animComponent.renderHeight)
    }
}