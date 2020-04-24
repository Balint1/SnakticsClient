package com.snake.game.states

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.google.gson.Gson
import com.snake.game.backend.Player
import com.snake.game.backend.SocketService
import com.snake.game.backend.SimpleResponse
import com.snake.game.backend.HttpService
import com.snake.game.backend.Events
import com.snake.game.backend.UpdatedList
import org.json.JSONObject
import javax.swing.plaf.nimbus.State

class Lobby(
    private val roomId: String,
    players: MutableList<Player>
) : MenuBaseState() {
    private val playerId: String = SocketService.socket.id()
    private val playersList = Table()

    init {
        addListeners()
        setTitle("Lobby")

        createScrollPane(playersList, ELEMENT_WIDTH * 2 / 3).apply {
            addElement(this)
        }

        initializePlayersList(players)

        val playBtn = createTextButton("Play", (ELEMENT_WIDTH - SPACING) / 3f) {
            showWaitDialog("Starting game...")
            HttpService.startGame(roomId, playerId, ::onStartGame)
        }

        val leaveBtn = createTextButton("Leave", (ELEMENT_WIDTH - SPACING) / 3f) {
            HttpService.leaveRoom(roomId, playerId, ::onLeaveRoom)
        }

        addElements(leaveBtn, playBtn, spacing = SPACING)
    }

    /**
     * Called when the player success or fails to to start the game
     *
     * @param response response fom create room http request
     */
    private fun onStartGame(response: SimpleResponse) {
        Gdx.app.debug("UI", "Lobby::onStartGame(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            cancelListeners()
            StateManager.push(GameState(roomId, playerId))

        } else {
            showMessageDialog(response.message)
        }
    }

    /**
     * Called when the player success or fails to to leave the room
     *
     * @param response response fom create room http request
     */
    private fun onLeaveRoom(response: SimpleResponse) {
        Gdx.app.debug("UI", "Lobby::onLeaveRoom(%b)".format(response.success))
        hideDialog()
        if (response.success) {
            cancelListeners()
            StateManager.set(MainMenu())
        } else {
            showMessageDialog(response.message)
        }
    }

    /**
     * Start listening to responses on owner change events
     */
    private fun addListeners() {
        SocketService.socket.on(Events.OWNER_CHANGED.value) { args ->
            Gdx.app.postRunnable {
                assignOwner(args)
            }
        }.on(Events.LEAVE_RESPONSE.value) { args ->
            Gdx.app.postRunnable {
                leaveSocketRoom(args)
            }
        }.on(Events.NEW_PLAYER.value) { args ->
            val data: JSONObject = args[0] as JSONObject
            val player: Player = Gson().fromJson(data.toString(), Player::class.java)
            Gdx.app.postRunnable {
                insertPlayer(player)
            }
        }.on(Events.PLAYER_LEFT_ROOM.value) { args ->
            val data: JSONObject = args[0] as JSONObject
            val updatedList: UpdatedList = Gson().fromJson(data.toString(), UpdatedList::class.java)
            Gdx.app.postRunnable {
                initializePlayersList(updatedList.players)
            }
        }.on(Events.START_GAME.value) {
            Gdx.app.postRunnable {
                cancelListeners()
                StateManager.push(GameState(roomId, playerId))
            }
        }
    }

    /**
     * cancels all extra listeners
     */
    private fun cancelListeners() {
        SocketService.socket.off(Events.OWNER_CHANGED.value)
        SocketService.socket.off(Events.LEAVE_RESPONSE.value)
        SocketService.socket.off(Events.JOIN_RESPONSE.value)
        SocketService.socket.off(Events.PLAYER_LEFT_ROOM.value)
    }

    private fun assignOwner(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val response: SimpleResponse = Gson().fromJson(data.toString(), SimpleResponse::class.java)
        Gdx.app.debug("UI", "Lobby::assignOwner(%b)".format(response.success))
        if (response.success) {
            showMessageDialog("Now you are the room owner ")
        }
    }

    private fun leaveSocketRoom(args: Array<Any>) {
        val data: JSONObject = args[0] as JSONObject
        val response: SimpleResponse = Gson().fromJson(data.toString(), SimpleResponse::class.java)
        if (!response.success) {
            Gdx.app.debug("UI", "Lobby::leaveSocketRoom(%b)".format(response.success))
        }
    }

    private fun initializePlayersList(players: MutableList<Player>) {
        playersList.clear()
        for (player: Player in players) {
            insertPlayer(player)
        }
    }

    private fun insertPlayer(player: Player) {
        val nicknameLabel = Label(player.nickname, skin, "title").apply {
            setSize(ELEMENT_WIDTH * 14 / 25, ELEMENT_HEIGHT / 2)
        }
        val ownerIndicator = if (player.owner)
            Image(Texture("indicators/owner.png")).apply {
                width = ICON_WIDTH
                height = width
            } else Image().apply {
            width = ICON_WIDTH
            height = width
        }
        addElements(
                nicknameLabel,
                ownerIndicator,
                spacing = ELEMENT_WIDTH / 25,
                parent = this.playersList,
                padTop = nicknameLabel.height / 2f
        )
    }
}