package com.snake.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

class AndroidLauncher : AndroidApplication() {
    private var game: SnakeGame? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        // Anti-aliasing
        config.numSamples = 2
        game = SnakeGame()
        initialize(game!!, config)
    }

    override fun onBackPressed() {
        game!!.onBackPressed()
    }
}