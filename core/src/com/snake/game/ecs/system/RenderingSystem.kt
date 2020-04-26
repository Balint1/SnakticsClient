package com.snake.game.ecs.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.CatmullRomSpline
import com.badlogic.gdx.math.Vector2
import com.snake.game.RenderingConstants
import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.ecs.component.AnimatedSpriteComponent
import com.snake.game.ecs.component.BouncingRenderComponent
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.PlayerComponent
import com.snake.game.ecs.component.PositionComponent
import com.snake.game.ecs.component.ShadowRenderComponent
import com.snake.game.ecs.component.SnakeComponent
import com.snake.game.ecs.component.SpriteComponent
import com.snake.game.ecs.component.TagComponent
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityManager
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

/**
 * Handles rendering entities that have a position and a sprite component.
 */
// object RenderingSystem: System(ComponentType.Position and (ComponentType.AnimatedSprite or ComponentType.Sprite)) {
object RenderingSystem : System(ComponentType.Position) {
    private val debugFont = BitmapFont()

    init {
        debugFont.data.setScale(3f)
        debugFont.color = Color.WHITE
    }

    override fun update(dt: Float, entity: Entity) {
        // Update bouncing animation components
        if (entity.hasComponent(ComponentType.BouncingRender)) {
            var bouncingComponent = entity.getComponent(ComponentType.BouncingRender) as BouncingRenderComponent

            var t = (bouncingComponent.time % bouncingComponent.duration) / bouncingComponent.duration
            bouncingComponent.offsetY = bouncingComponent.amplitude * max(0f, sin((t - 0.5) * 2 * Math.PI).toFloat())
            bouncingComponent.time += dt
        }
    }

    override fun render(sb: SpriteBatch, em: EntityManager, worldWidth: Float, worldHeight: Float) {
        super.render(sb, em, worldWidth, worldHeight)

        val entities = em.getEntities(componentTypes)

        val sr = ShapeRenderer()
        sr.projectionMatrix = sb.projectionMatrix

        // Background
        sr.begin(ShapeType.Filled)
        sr.color = RenderingConstants.BACKGROUND_COLOR
        sr.rect(0f, 0f, worldWidth, worldHeight)
        sr.end()

        entities.map { entity ->
            val pos = entity.getComponent(ComponentType.Position) as PositionComponent

            // Render shadow
            if (entity.hasComponent(ComponentType.ShadowRender)) {
                var shadowRenderC = entity.getComponent(ComponentType.ShadowRender) as ShadowRenderComponent
                sr.begin(ShapeType.Filled)
                sr.color = Color(0f, 0f, 0f, 0.2f)
                sr.ellipse(pos.x, pos.y, shadowRenderC.width, shadowRenderC.height + shadowRenderC.offsetY)
                sr.end()
            }

            if (entity.hasComponent(ComponentType.Sprite) || entity.hasComponent(ComponentType.AnimatedSprite)) {
                var y = pos.y

                // Handle bouncing animation rendering
                if (entity.hasComponent(ComponentType.BouncingRender))
                    y += (entity.getComponent(ComponentType.BouncingRender) as BouncingRenderComponent).offsetY

                if (entity.hasComponent(ComponentType.Sprite))
                    render(sb, entity.getComponent(ComponentType.Sprite) as SpriteComponent, pos.x, y)
                if (entity.hasComponent(ComponentType.AnimatedSprite))
                    render(sb, entity.getComponent(ComponentType.AnimatedSprite) as AnimatedSpriteComponent, pos.x, y)
            }

            if (entity.hasComponent(ComponentType.Tag)) {
                val tag = (entity.getComponent(ComponentType.Tag) as TagComponent).tag ?: return

                // Render snake head
                if (tag == TagComponent.EntityTagType.SnakeHead) {
                    renderSnake(em, entity, sr)
                } else if (tag == TagComponent.EntityTagType.SnakeBody) {
                } else if (tag == TagComponent.EntityTagType.Fireball) {
                    sr.begin(ShapeType.Filled)
                    sr.color = Color.RED
                    sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                    sr.end()
                } else if (tag == TagComponent.EntityTagType.Food) {
                    sr.begin(ShapeType.Filled)
                    sr.color = Color.GREEN
                    sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                    sr.end()
                }
            } else {
                // Temporary render
                sr.begin(ShapeType.Filled)
                sr.color = Color.RED
                if (entity.getComponent(ComponentType.Tag) != null)
                    sr.color = Color.ORANGE
                sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                sr.end()
            }
        }
    }

    /**
     * Handles rendering of a SpriteComponent at the given position.
     */
    private fun render(sb: SpriteBatch, spriteComponent: SpriteComponent, x: Float, y: Float) {
        sb.begin()
        sb.draw(spriteComponent.sprite, x, y, spriteComponent.renderWidth, spriteComponent.renderHeight)
        sb.end()
    }

    /**
     * Handles rendering of an AnimatedSpriteComponent at the given position.
     */
    private fun render(sb: SpriteBatch, animComponent: AnimatedSpriteComponent, x: Float, y: Float) {
        sb.begin()
        // Draw the current frame of this animation
        sb.draw(animComponent.getCurrentFrame(), x, y, animComponent.renderWidth, animComponent.renderHeight)
        sb.end()
    }

    /**
     * Renders a snake.
     * @param sb the game spritebatch
     * @param em the entity manager
     * @param snakeHead the first snake piece
     */
    private fun renderSnake(em: EntityManager, snakeHead: Entity, shapeRenderer: ShapeRenderer) {
        var playerComponent = snakeHead.getComponent(ComponentType.Player) as PlayerComponent

        // Stop rendering the snake once it is dead and decayed
        if (!playerComponent.alive && !playerComponent.decaying)
            return

        var pieceId = (snakeHead.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
        var headPosition = snakeHead.getComponent(ComponentType.Position) as PositionComponent

        val snakePoints: ArrayList<Vector2> = ArrayList()
        // Add the head position twice to force the CatmullRomSpline to start there
        snakePoints.add(Vector2(headPosition.x, headPosition.y))
        snakePoints.add(Vector2(headPosition.x, headPosition.y))

        var pos: PositionComponent? = null
        while (pieceId != null) {
            if (!em.hasEntity(pieceId))
                break
            var entity = em.getEntity(pieceId)!!
            pos = entity.getComponent(ComponentType.Position) as PositionComponent
            pieceId = (entity.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
            snakePoints.add(Vector2(pos.x, pos.y))
        }

        // Add the last position twice to force the CatmullRomSpline to end there
        if (pos != null)
            snakePoints.add(Vector2(pos.x, pos.y))

        var startColor = Color(0.3f, 0.75f, 0.8f, 1.0f)
        var endColor = Color(0.20f, 0.45f, 0.7f, 1.0f)

        // Handle blinding during decay
        var alpha = 1.0f
        if (!playerComponent.alive && playerComponent.decaying) {
            val blinkTicks = 30f
            var t = (RenderingConstants.SNAKE_DECAYING_TICKS - playerComponent.remainingDecayTicks) / blinkTicks.toFloat()
            t = t.pow(1.25f) // makes bounces faster and faster
            alpha = 1f - max(0f, sin(2f*Math.PI * (t - 0.5f)).toFloat())
            startColor = Color(0.2f, 0.2f, 0.2f, alpha)
            endColor = Color(0.12f, 0.12f, 0.12f, alpha)
        }

        // number of points used for rendering the spline
        var pointsDensity = 5
        var k = if (snakePoints.size > 2) snakePoints.size * pointsDensity else 1

        val spline = CatmullRomSpline<Vector2>(snakePoints.toTypedArray(), false)
        var points = ArrayList<Vector2>()
        var alphas = ArrayList<Float>()

        for (i in 0 until k) {
            var pt = Vector2()
            spline.valueAt(pt, i / (k - 1).toFloat())
            points.add(pt)
            alphas.add(alpha)
        }

        // In invisible mode, set the first alphas to 0 and then fade to 1
        if (playerComponent.invisible && playerComponent.alive) {
            var invisibilityLength = 4 * pointsDensity
            for (i in 0 until min(invisibilityLength, alphas.size))
                alphas[i] = 0f
            for (i in invisibilityLength until min(invisibilityLength * 2, alphas.size))
                alphas[i] = (i - invisibilityLength) / invisibilityLength.toFloat()

            // If the invisible player is the local player, set a minimum opacity of 0.1
            if (playerComponent.playerId == SnakeECSEngine.localPlayerId) {
                for (i in 0 until min(invisibilityLength * 2, alphas.size))
                    alphas[i] = max(alphas[i], 0.2f)
            }
        }

        shapeRenderer.begin(ShapeType.Filled)

        // Draw shadow
        for (i in 0 until points.size) {
            shapeRenderer.color.set(Color(0f, 0f, 0f, 0.1f*alphas[i]))
            shapeRenderer.circle(points[i].x, points[i].y - 2, RenderingConstants.SNAKE_CIRCLE_RADIUS)
        }

        // Draw colored snake
        for (i in 0 until points.size) {
            shapeRenderer.color.set(startColor).lerp(endColor, i / points.size.toFloat())
            shapeRenderer.color.a = alphas[i]
            shapeRenderer.circle(points[i].x, points[i].y, RenderingConstants.SNAKE_CIRCLE_RADIUS)
        }

        shapeRenderer.end()
    }
}