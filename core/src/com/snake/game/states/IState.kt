package com.snake.game.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Interface for states managed by the StateManager
 *
 * @see StateManager
 */
interface IState {
    /**
     * Called when the state is activated
     *
     */
    fun activated()

    /**
     * Called when the state is deactivated
     *
     */
    fun deactivated()

    /**
     * Render any elements in the state
     *
     * @param sb
     */
    fun render(sb: SpriteBatch)

    /**
     * Update elements in the state
     *
     * @param dt
     */
    fun update(dt: Float)

    /**
     * Dispoce of any elements in the state
     *
     */
    fun dispose()

    /**
     * Called when the window is resized
     *
     * @param width The new width of the window
     * @param height The new height of the window
     */
    fun resize(width: Int, height: Int)

    /**
     * Called when the physical back button is pressed
     *
     */
    fun onBackPressed() {
        StateManager.pop()
    }
}