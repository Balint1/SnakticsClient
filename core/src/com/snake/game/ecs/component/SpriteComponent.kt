package com.snake.game.ecs.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

class SpriteComponent(
        spriteFile: String,
        var renderWidth: Float,
        var renderHeight: Float): Component(ComponentType.Sprite) {

    var sprite: Texture? = Texture(Gdx.files.internal(spriteFile))

}