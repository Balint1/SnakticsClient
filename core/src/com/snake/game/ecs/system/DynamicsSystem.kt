package com.snake.game.ecs.system

import com.badlogic.gdx.Gdx
import com.snake.game.ecs.component.ComponentType
import com.snake.game.ecs.entity.Entity

object DynamicsSystem: System(listOf(ComponentType.Position)) {
    override fun update(dt: Float, entity: Entity) {

    }

}