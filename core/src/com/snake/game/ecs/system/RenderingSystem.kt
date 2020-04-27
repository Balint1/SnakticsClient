package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
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

                when (tag) {
                    TagComponent.EntityTagType.SnakeHead -> {
                        /*sr.begin(ShapeType.Filled)
                        sr.color = Color.RED
                        sr.rect(pos.x - 2, pos.y - 2, 4f, 4f)
                        sr.end()*/
                        renderSnake(em, entity, sr)
                    }
                    TagComponent.EntityTagType.SnakeBody -> {
                        /*sr.begin(ShapeType.Filled)
                        sr.color = Color.YELLOW
                        sr.rect(pos.x - 2, pos.y - 2, 4f, 4f)
                        sr.end()*/
                    }
                    TagComponent.EntityTagType.Fireball -> {
                        sr.begin(ShapeType.Filled)
                        sr.color = Color.RED
                        sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                        sr.end()
                    }
                    TagComponent.EntityTagType.Food -> {
                        sr.begin(ShapeType.Filled)
                        sr.color = Color.GREEN
                        sr.rect(pos.x - 10, pos.y - 10, 20f, 20f)
                        sr.end()
                    }
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

        // Draw border around
        var borderMargin = 3f
        sr.begin(ShapeType.Line)
        sr.color = Color.RED
        sr.rect(borderMargin, borderMargin, worldWidth - 2*borderMargin, worldHeight - 2*borderMargin)
        sr.end()
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
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        var playerComponent = snakeHead.getComponent(ComponentType.Player) as PlayerComponent

        // Stop rendering the snake once it is dead and decayed
        if (!playerComponent.alive && !playerComponent.decaying)
            return

        var pieceId = (snakeHead.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
        var headPosition = snakeHead.getComponent(ComponentType.Position) as PositionComponent

        val snakePoints: ArrayList<Vector2> = ArrayList()
        snakePoints.add(Vector2(headPosition.x, headPosition.y))

        while (pieceId != null) {
            if (!em.hasEntity(pieceId))
                break
            var entity = em.getEntity(pieceId)!!
            var pos = entity.getComponent(ComponentType.Position) as PositionComponent
            snakePoints.add(Vector2(pos.x, pos.y))
            if(!entity.hasComponent(ComponentType.Snake))
                break
            pieceId = (entity.getComponent(ComponentType.Snake) as SnakeComponent).nextPieceId
        }

        var startColor = Color(0.3f, 0.75f, 0.8f, 1.0f)
        var endColor = Color(0.20f, 0.45f, 0.7f, 1.0f)

        if (playerComponent.color == "BlueSnake") {
            startColor = Color(0.3f, 0.75f, 0.8f, 1.0f)
            endColor = Color(0.20f, 0.45f, 0.7f, 1.0f)
        } else if (playerComponent.color == "GreenSnake") {
            startColor = Color(0.235f, 0.980f, 0f, 1.0f)
            endColor = Color(0.552f, 0.886f, 0.678f, 1.0f)
        }

        // Handle blinking during decay
        var alpha = 1.0f
        if (!playerComponent.alive && playerComponent.decaying) {
            val blinkTicks = 30f
            var t: Float = (RenderingConstants.SNAKE_DECAYING_TICKS - playerComponent.remainingDecayTicks) / blinkTicks
            t = t.pow(1.25f) // blinks faster and faster
            alpha = 1f - max(0f, sin(2f*Math.PI * (t - 0.5f)).toFloat())
            startColor = Color(0.8f, 0.2f, 0.2f, alpha)
            endColor = Color(0.6f, 0.12f, 0.12f, alpha)
        }

        // Number of interpolated points between two snake body pieces
        var pointsDensity = 3

        // Group points into array of close points (prevents issues when one side of the snake is on the other side of the board)
        var splines: ArrayList<CatmullRomSpline<Vector2>> = ArrayList()
        var currentSplinePoints: ArrayList<Vector2> = ArrayList()
        currentSplinePoints.add(snakePoints[0])

        for (i in 1 until snakePoints.size) {
            var pre = snakePoints[i - 1]
            var p = snakePoints[i]
            if ((p.x - pre.x).pow(2) + (p.y - pre.y).pow(2) > (RenderingConstants.SNAKE_CIRCLE_RADIUS * 4).pow(2)) {
                currentSplinePoints.add(pre) // force the spline to go all the way to the last point
                splines.add(CatmullRomSpline<Vector2>(currentSplinePoints.toTypedArray(), false))
                currentSplinePoints.clear()
                currentSplinePoints.add(p) // force the spline to start at that point
            }
            currentSplinePoints.add(p)
        }
        currentSplinePoints.add(snakePoints[snakePoints.size - 1])
        splines.add(CatmullRomSpline<Vector2>(currentSplinePoints.toTypedArray(), false))

        // Use the splines to interpolate an a list of points
        var points = ArrayList<Vector2>()
        for (s in splines) {
            var k = if (s.controlPoints.size > 2) s.controlPoints.size * pointsDensity else 0
            if (s.controlPoints.size <= 2) {
                for (p in s.controlPoints)
                    points.add(p)
            } else {
                for (i in 0 until k) {
                    var pt = Vector2()
                    s.valueAt(pt, i / (k - 1).toFloat())
                    points.add(pt)
                }
            }
        }

        var alphas = ArrayList<Float>()
        for (i in 0 until points.size)
            alphas.add(alpha)

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

        // Draw shadow
        shapeRenderer.begin(ShapeType.Filled)
        for (i in 0 until points.size) {
            shapeRenderer.color.set(Color(0f, 0f, 0f, 0.1f*alphas[i]))
            shapeRenderer.circle(points[i].x, points[i].y - 2, RenderingConstants.SNAKE_CIRCLE_RADIUS)
        }
        shapeRenderer.end()

        // Draw border around local snake
        if (playerComponent.playerId == SnakeECSEngine.localPlayerId) {
            shapeRenderer.begin(ShapeType.Filled)
            shapeRenderer.color = Color(0.83f, 0.69f, 0.22f, 1f)
            for (i in 0 until points.size - 1) {
                shapeRenderer.color.a = alphas[i] * 0.5f
                shapeRenderer.circle(points[i].x, points[i].y, RenderingConstants.SNAKE_CIRCLE_RADIUS + 0.6f)
            }
            shapeRenderer.end()
        }

        // Draw the snake
        shapeRenderer.begin(ShapeType.Filled)
        for (i in 0 until points.size) {
            shapeRenderer.color.set(startColor).lerp(endColor, i / points.size.toFloat())
            shapeRenderer.color.a = alphas[i]
            shapeRenderer.circle(points[i].x, points[i].y, RenderingConstants.SNAKE_CIRCLE_RADIUS)
        }
        shapeRenderer.end()
    }
}