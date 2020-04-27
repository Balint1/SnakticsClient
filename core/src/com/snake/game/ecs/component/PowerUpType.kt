package com.snake.game.ecs.component

import com.snake.game.RenderingConstants

enum class PowerUpType(val internalName: String, val spriteFile: String?, val spriteSize: Float) {
    SpeedBooster("speedBooster", PowerUpType.SPRITES_LOCATION + "powerup-speed-v1.png"),
    SpeedDebuff("speedDebuff", PowerUpType.SPRITES_LOCATION + "powerup-slow-v2.png"),
    InvisibleAbility("invisibleAbility", PowerUpType.SPRITES_LOCATION + "powerup-invisible.png"),
    Fireball("Fireball", PowerUpType.SPRITES_LOCATION + "powerup-fire-ball-32bits.png"),
    ColorSwap("ColorSwap", PowerUpType.SPRITES_LOCATION + "powerup-colorswap-v1.png"),
    Wall("Wall", PowerUpType.SPRITES_LOCATION + "powerup-tw-32bits.png");


    constructor(internalName: String) : this(internalName, null, 0f)
    constructor(internalName: String, spriteFile: String) : this(internalName, spriteFile, RenderingConstants.POWERUP_SPRITE_SIZE)

    companion object {
        const val SPRITES_LOCATION = "powerup-sprites/"

        fun fromName(name: String): PowerUpType? {
            for (type in PowerUpType.values()) {
                if (name == type.internalName)
                    return type
            }
            return null
        }
    }
}