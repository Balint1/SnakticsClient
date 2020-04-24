package com.snake.game.ecs.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class AnimatedSpriteComponent(
    spritesheetFile: String,
    spritesheetCols: Int,
    spritesheetRows: Int,
    frameDuration: Float,
    val renderWidth: Float,
    val renderHeight: Float
) : Component(ComponentType.AnimatedSprite) {

    var animation: Animation<TextureRegion>? = null

    // Track elapsed time of the animation
    var time: Float = 0f

    constructor(spritesheetFile: String, numberOfSprites: Int, frameDuration: Float, renderWidth: Float, renderHeight: Float) :
            this(spritesheetFile, numberOfSprites, 1, frameDuration, renderWidth, renderHeight)

    init {
        // Load the Texture
        val sheet = Texture(Gdx.files.internal(spritesheetFile))

        // Create a matrix of TextureRegions, assuming all frames in the spritesheet have the same size.
        val textureRegionMatrix = TextureRegion.split(sheet, sheet.width / spritesheetCols, sheet.height / spritesheetRows)

        // Turn that matrix into a 1D array (top left to bottom right)
        val frames: com.badlogic.gdx.utils.Array<TextureRegion> = com.badlogic.gdx.utils.Array(textureRegionMatrix.flatten().toTypedArray())

        // Create the Animation with the frame interval and frames array
        this.animation = Animation(frameDuration, frames)
    }

    /**
     * Get the current frame in the animation.
     * @return the current frame to display.
     */
    fun getCurrentFrame(): TextureRegion? {
        return this.animation!!.getKeyFrame(this.time, true)
    }

    // TODO Remember to dispose() th texture at some point
}