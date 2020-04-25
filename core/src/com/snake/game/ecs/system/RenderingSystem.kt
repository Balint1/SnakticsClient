package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.snake.game.ecs.component.*
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityManager
import kotlin.math.sin

/**
 * Handles rendering entities that have a position and a sprite component.
 */
// object RenderingSystem: System(ComponentType.Position and (ComponentType.AnimatedSprite or ComponentType.Sprite)) {
object RenderingSystem : System(ComponentType.Position) {
    // Rendering constants // TODO move to other file?
    private const val SNAKE_RADIUS = 4f
    private const val SNAKE_THICKNESS = 3f

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
        sr.begin(ShapeType.Filled)

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
                val tag = (entity.getComponent(ComponentType.Tag) as TagComponent).tag ?: continue

                // Render snake head
                if(tag == TagComponent.EntityTagType.SnakeHead) {
                    //sr.color = Color.FOREST
                    //sr.circle(pos.x, pos.y, SNAKE_RADIUS * 1.5f)
                    renderSnake(sb, em, entity, sr)
                }
                else if (tag == TagComponent.EntityTagType.SnakeBody) {

                }
                else {
                    // Temporary render
                    sr.color = Color.YELLOW
                    sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)

                    debugFont.draw(sb, "TEST STRING", 0f, 0f)
                    debugFont.draw(sb, tag.typeString, pos.x, pos.y)
                }
            } else {
                // Temporary render
                sr.color = Color.RED
                sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
            }
        }

        sr.end()
    }

    /**
     * Handles rendering of a SpriteComponent at the given position.
     */
    private fun render(sb: SpriteBatch, spriteComponent: SpriteComponent, x: Float, y: Float) {
        sb.draw(spriteComponent.sprite, x, y, spriteComponent.renderWidth, spriteComponent.renderHeight)
    }

    /**
     * Handles rendering of an AnimatedSpriteComponent at the given position.
     */
    private fun render(sb: SpriteBatch, animComponent: AnimatedSpriteComponent, x: Float, y: Float) {
        // Draw the current frame of this animation
        sb.draw(animComponent.getCurrentFrame(), x, y, animComponent.renderWidth, animComponent.renderHeight)
    }

    /**
     * Renders a snake.
     * @param sb the game spritebatch
     * @param em the entity manager
     * @param snakeHead the first snake piece
     */
    private fun renderSnake(sb: SpriteBatch, em: EntityManager, snakeHead: Entity, shapeRenderer: ShapeRenderer) {
        var playerComponent = snakeHead.getComponent(ComponentType.Player) as PlayerComponent

        // Stop rendering the snake once it is dead and decayed
        if(!playerComponent.alive && !playerComponent.decaying)
            return

        var pieceId = (snakeHead.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
        var headPosition = snakeHead.getComponent(ComponentType.Position) as PositionComponent

        val snakePoints: ArrayList<Vector2> = ArrayList()
        // Add the head position twice to force the CatmullRomSpline to start there
        snakePoints.add(Vector2(headPosition.x, headPosition.y))
        snakePoints.add(Vector2(headPosition.x, headPosition.y))

        var pos: PositionComponent? = null
        while(pieceId != null) {
            if (!em.hasEntity(pieceId))
                break;
            var entity = em.getEntity(pieceId)!!
            pos = entity.getComponent(ComponentType.Position) as PositionComponent
            pieceId = (entity.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
            snakePoints.add(Vector2(pos.x, pos.y))
        }

        // Add the last position twice to force the CatmullRomSpline to end there
        if(pos != null)
            snakePoints.add(Vector2(pos!!.x, pos!!.y))

        var startColor = Color(0.3f, 0.75f, 0.8f, 1.0f)
        var endColor = Color(0.20f, 0.45f, 0.7f, 1.0f)

        // Handle blinding during decay
        var alpha = 1.0f
        if(!playerComponent.alive && playerComponent.decaying) {
            var blinkTicks = 20
            alpha = sin(Math.PI * (playerComponent.remainingDecayTicks % blinkTicks) / blinkTicks).toFloat()
            startColor = Color(0.2f, 0.2f, 0.2f, alpha)
            endColor = Color(0.12f, 0.12f, 0.12f, alpha)
        }

        // number of points used for rendering the spline
        var k = if(snakePoints.size > 2) snakePoints.size * 5 else 1

        val spline = CatmullRomSpline<Vector2>(snakePoints.toTypedArray(), false)
        var points = ArrayList<Vector2>()

        for(i in 0 until k) {
            var pt = Vector2()
            spline.valueAt(pt, i / (k-1).toFloat())
            points.add(pt)
        }

        // Draw shadow
        shapeRenderer.color.set(Color(0f, 0f, 0f, 0.1f*alpha))
        for(i in 0 until points.size)
            shapeRenderer.circle(points[i].x, points[i].y - 2, SNAKE_RADIUS)

        // Draw colored snake
        for(i in 0 until points.size) {
            shapeRenderer.color.set(startColor).lerp(endColor, i / points.size.toFloat())
            shapeRenderer.circle(points[i].x, points[i].y, SNAKE_RADIUS)
        }
    }
}