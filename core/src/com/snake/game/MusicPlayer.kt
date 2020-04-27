package com.snake.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music

/*
Implement Music in the game, with a singleton (object in kotlin)
Path to the musics are in the config file
volume is set in the preferences file
 */
object MusicPlayer {
    val music: Music = Gdx.audio.newMusic(Gdx.files.internal(Config.MUSIC_PATH))

    init {
        music.volume = Preferences.volume
        music.isLooping = true
        music.play()
    }
    public fun play() {
        music.play()
    }
    public fun pause() {
        music.pause()
    }
    public fun updateVolume() {
        music.volume = Preferences.volume
    }
}