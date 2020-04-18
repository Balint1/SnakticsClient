package com.snake.game.ecs

import com.badlogic.gdx.Gdx
import com.snake.game.ecs.component.PositionComponent
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.entity.EntityFactory
import com.snake.game.ecs.system.AnimationSystem
import com.snake.game.ecs.system.ComponentSyncSystem
import com.snake.game.ecs.system.RenderingSystem
import java.util.*

object SnakeECSEngine: ECSEngine(
        ComponentSyncSystem, AnimationSystem, RenderingSystem) {
    init {

    }

    fun createEntities() {
        entityManager.clearEntities()

        val factory = EntityFactory

        // Testing
        //var test = Entity(UUID.randomUUID().toString(), entityManager)
        //test.addComponent(PositionComponent())


        //for(i in 0..3)
            //factory.createSnake(entityManager)


    }
}