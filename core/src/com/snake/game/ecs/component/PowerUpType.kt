package com.snake.game.ecs.component

enum class PowerUpType(val internalName: String, val spriteFile: String?, val spriteSize: Float) {
    SpeedBooster("speedBooster", PowerUpType.SPRITES_LOCATION + "powerup-slow-v2.png", 100f),
    SpeedDebuff("speedDebuff", PowerUpType.SPRITES_LOCATION + "powerup-speed-v1.png", 100f),
    InvisibleAbility("invisibleAbility", PowerUpType.SPRITES_LOCATION + "powerup-invisible.png", 100f);

    constructor(internalName: String) : this(internalName, null, 0f)

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