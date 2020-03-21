package com.snake.game.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch

import java.util.Stack

object StateManager {
    private val states: Stack<IState> by lazy { Stack<IState>() }

    fun push(state: IState) {
        states.push(state)
    }

    fun set(state: IState) {
        states.pop().dispose()
        states.push(state)
    }

    fun update(dt: Float) {
        states.peek().update(dt)
    }

    fun render(sb: SpriteBatch) {
        states.peek().render(sb)
    }
}