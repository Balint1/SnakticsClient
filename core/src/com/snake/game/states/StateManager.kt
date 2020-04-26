package com.snake.game.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch

import java.util.Stack

object StateManager {
    private val states: Stack<IState> by lazy { Stack<IState>() }

    fun numStates(): Int {
        return states.size
    }

    fun push(state: IState) {
        if (!states.empty())
            states.peek().deactivated()
        states.push(state)
        state.activated()

        Gdx.app.debug("States", "StateManager::push - Active state: %s".format(states.peek().javaClass.kotlin.qualifiedName))
    }

    fun pop() {
        val old = states.pop()
        old.deactivated()
        old.dispose()

        if (states.empty())
            Gdx.app.exit()
        else
            states.peek().activated()

        Gdx.app.debug("States", "StateManager::pop - Active state: %s".format(states.peek().javaClass.kotlin.qualifiedName))
    }

    fun set(state: IState) {
        while (!states.empty()) {
            val old = states.pop()
            old.deactivated()
            old.dispose()
        }
        states.push(state)
        state.activated()

        Gdx.app.debug("States", "StateManager::set - Active state: %s".format(states.peek().javaClass.kotlin.qualifiedName))
    }

    fun update(dt: Float) {
        states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }

    fun resize(width: Int, height: Int) {
        for (s in states) {
            s.resize(width, height)
        }
    }

    fun onBackPressed() {
        states.peek().onBackPressed()
    }
}