package com.snake.game.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch

import java.util.Stack

object StateManager {
    private val states: Stack<IState> by lazy { Stack<IState>() }

    fun push(state: IState) {
        if (!states.empty())
            states.peek().hide()
        states.push(state)
        state.show()
    }

    fun pop() {
        val old = states.pop()
        old.hide()
        old.dispose()
        states.peek().show()
    }

    fun set(state: IState) {
        while (!states.empty()) {
            val old = states.pop()
            old.hide()
            old.dispose()
        }
        states.push(state)
        state.show()
    }

    fun update(dt: Float) {
        states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }
}