package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.snake.game.backend.*
import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.componentTypeFromInternalName
import com.snake.game.ecs.component.createComponent
import com.snake.game.ecs.entity.Entity
import org.json.JSONException
import org.json.JSONObject


class GameState(private val roomId: String, private val playerId: String) : MenuBaseState() {
    private val slider: Slider = Slider(-3f, 3f, 1f, false, skin)

    // TODO get from backend
    private val FIELD_WIDTH: Float = 500f
    private val FIELD_HEIGHT: Float = 300f

    private val ecs = SnakeECSEngine
    private val cam = OrthographicCamera()
    private var viewport = ExtendViewport(FIELD_WIDTH, FIELD_HEIGHT, FIELD_WIDTH, FIELD_HEIGHT, cam)

    init {
        cancelListeners()
        addListeners()

        slider.value = 0f
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                emitSliderValue(slider.value.toInt())
            }
        })
        addElement(slider)

        createTextButton("back") {
            HttpService.leaveRoom(roomId, playerId, ::onGameLeft)
            StateManager.pop()
        }.apply {
            addElement(this)
        }
    }

    override fun activated() {
        super.activated()

        updateViewport()
        ecs.createEntities()
    }

    private fun emitSliderValue(value: Int) {
        SocketService.socket.emit(Events.SLIDER_CHANGE.value, Data.SLIDER_CHANGE(value))
    }

    private fun addListeners() {
        SocketService.socket.on(Events.UPDATE.value) { args ->
            onStateUpdate(args)
        }
    }

    private fun cancelListeners() {
        SocketService.socket.off(Events.OWNER_CHANGED.value)
        SocketService.socket.off(Events.PLAYER_LEFT.value)
    }

    private fun onStateUpdate(args: Array<Any>) {
        val em = ecs.entityManager

        val data: JSONObject = args[0] as JSONObject
        try {
            val state = data.getJSONArray("state")
            Gdx.app.log("SocketIO", "state: $state")

            for(i in 0 until state.length()) {
                val componentData = state.getJSONObject(i)
                val id: String = componentData.getString("entityId");
                val componentTypeName = componentData.getString("componentType")

                var componentType: ComponentType? = componentTypeFromInternalName(componentTypeName)
                        ?: continue // skip if component type doesn't exist

                // Create the entity if necessary
                if(!em.hasEntity(id))
                    Entity(id, em)
                var entity = em.getEntity(id)!!

                // Create the component if necessary
                if(!entity.hasComponent(componentType!!))
                    entity.addComponent(createComponent(componentType))

                var component = entity.getComponent(componentType)!!
                component.updateFromJSON(componentData)

            }

        } catch (e: JSONException) {
            Gdx.app.log("SocketIO", "Error getting attributes: $e")
        }
    }

    override fun update(dt: Float) {
        super.update(dt)

        ecs.update(dt)
    }

    override fun render(sb: SpriteBatch) {
        super.render(sb)

        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        sb.projectionMatrix = cam.combined

        var sr = ShapeRenderer()
        sr.projectionMatrix = sb.projectionMatrix
        sr.begin(ShapeRenderer.ShapeType.Filled)
        sr.color = Color.DARK_GRAY
        sr.rect(0f,0f,10000f,10000f)
        sr.end()

        ecs.render(sb)
    }

    override fun resize(width: Int, height: Int) {
        updateViewport()
    }

    private fun updateViewport() {
        var ratio = FIELD_WIDTH / FIELD_HEIGHT
        viewport.update((Gdx.graphics.height * ratio).toInt(), Gdx.graphics.height, true)
    }

    /**
     * Called when the player success or fails to to leave the game
     *
     * @param response response fom create room http request
     */
    private fun onGameLeft(response: SimpleResponse) {
        Gdx.app.debug("UI", "GameState::onGameLeft(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.set(MainMenu())
        } else {
            showMessageDialog(response.message)
        }
    }
}