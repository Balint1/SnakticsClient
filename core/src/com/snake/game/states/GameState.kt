package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.snake.game.backend.*
import com.snake.game.controls.JoystickInput
import com.snake.game.controls.SwipeDetector
import com.snake.game.ecs.SnakeECSEngine
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.component.componentTypeFromInternalName
import com.snake.game.ecs.component.createComponent
import com.snake.game.ecs.entity.Entity
import org.json.JSONException
import org.json.JSONObject


class GameState(private val roomId: String, private val playerId: String) : BaseState() {
    private val swipeDetector = SwipeDetector

    // TODO get from backend
    private val FIELD_WIDTH: Float = 500f
    private val FIELD_HEIGHT: Float    = 300f

    private val ecs = SnakeECSEngine
    private val gameWidget = GameWidget(ecs, FIELD_WIDTH, FIELD_HEIGHT)

    init {
        cancelListeners()
        addListeners()

        val uiGroup = VerticalGroup()

        val splitPane = SplitPane(uiGroup, gameWidget, false,  skin)
        splitPane.setBounds(0f,0f, MenuBaseState.VIRTUAL_WIDTH, MenuBaseState.VIRTUAL_HEIGHT)
        splitPane.splitAmount = 0.2f
        splitPane.minSplitAmount = 0.2f
        splitPane.maxSplitAmount = 0.2f

        stage.addActor(splitPane)
        //element can be added to  the side panel with :
        //uiGroup.addActor(joystickInput.touchpad)


        createTextButton("back") {
            HttpService.leaveRoom(roomId, playerId, ::onGameLeft)
            StateManager.pop()
        }.apply {
            uiGroup.addActor(this)
        }

        // we add swipe listener :
        swipeDetector.active = true

    }

    override fun activated() {
        super.activated()

        ecs.createEntities()
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
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
    }

    /**
     * Called when the player success or fails to to leave the game
     *
     * @param response response from create room http request
     */
    private fun onGameLeft(response: SimpleResponse) {
        /*
        Gdx.app.debug("UI", "GameState::onGameLeft(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            StateManager.set(MainMenu())
        } else {
            showMessageDialog(response.message)
        }
        */

        // if he succed we stop the swipe detector
        swipeDetector.active = false
    }
}


class GameWidget(val ecs: SnakeECSEngine,
                 private val fieldWidth: Float,
                 private val fieldHeight: Float): Widget() {

    val viewport = ExtendViewport(fieldWidth, fieldHeight, fieldWidth, fieldHeight)

    // The SpriteBatch used for rendering the game
    val sb = SpriteBatch()

    fun updateSize() {

        width = MenuBaseState.VIRTUAL_WIDTH * 0.8f
        height = MenuBaseState.VIRTUAL_HEIGHT

        viewport.update(width.toInt(), height.toInt(), true)
        viewport.setScreenBounds(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        /*var ratio: Float = fieldWidth / fieldHeight
        var h = height
        var gwidth = (h * ratio)
        viewport.update(gwidth.toInt(), h.toInt(), true)*/
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        // TODO update only when necessary
        updateSize()

        viewport.apply(true)
        sb.projectionMatrix = viewport.camera.combined
        sb.begin()
        ecs.render(sb, fieldWidth, fieldHeight)
        sb.end()
    }
}