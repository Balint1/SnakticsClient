package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.snake.game.ecs.component.*
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityManager


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


        // Background
        sr.color = BACKGROUND_COLOR
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.rect(0f, 0f, worldWidth, worldHeight)
        sr.end()

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
                    sr.begin(ShapeType.Filled)
                    //sr.color = Color.FOREST
                    //sr.circle(pos.x, pos.y, SNAKE_RADIUS * 1.5f)
                    renderSnake(sb, em, entity, sr)
                    sr.end()
                }
                else if (tag == TagComponent.EntityTagType.SnakeBody) {

                }
                else {
                    // Temporary render
                    sr.begin(ShapeRenderer.ShapeType.Filled)
                    sr.color = Color.YELLOW
                    sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                    sr.end()

                    debugFont.draw(sb, "TEST STRING", 0f, 0f)
                    debugFont.draw(sb, tag.typeString, pos.x, pos.y)
                }
            } else {
                // Temporary render
                sr.begin(ShapeRenderer.ShapeType.Filled)
                sr.color = Color.RED
                sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                sr.end()
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
        val snakePoints: ArrayList<Vector2> = ArrayList()
        var pieceId = (snakeHead.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId

        var time = java.lang.System.nanoTime()

        while(pieceId != null) {
            var entity = em.getEntity(pieceId)!!
            val pos = entity.getComponent(ComponentType.Position) as PositionComponent
            pieceId = (entity.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
            snakePoints.add(Vector2(pos.x, pos.y))
        }

        val startColor = Color(0.3f, 0.75f, 0.8f, 1.0f)
        val endColor = Color(0.20f, 0.45f, 0.7f, 1.0f)

        // number of points used for rendering the spline
        var k = snakePoints.size * 5

        val spline = CatmullRomSpline<Vector2>(snakePoints.toTypedArray(), false)
        var points = ArrayList<Vector2>()

        for(i in 0 until k) {
            var pt = Vector2()
            spline.valueAt(pt, i / k.toFloat())
            points.add(pt)
        }

        // Draw shadow
        shapeRenderer.color.set(Color(0f, 0f, 0f, 0.1f))
        for(i in 0 until points.size)
            shapeRenderer.circle(points[i].x, points[i].y - 2, SNAKE_RADIUS)

        // Draw colored snake
        for(i in 0 until points.size) {
            shapeRenderer.color.set(startColor).lerp(endColor, i / points.size.toFloat())
            shapeRenderer.circle(points[i].x, points[i].y, SNAKE_RADIUS)
        }

        //Gdx.app.log("snake_render", "rendering time=" + ((java.lang.System.nanoTime() - time) / 1000000f).toString())
    }
}