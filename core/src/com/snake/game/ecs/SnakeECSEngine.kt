package com.snake.game.ecs

import com.badlogic.gdx.Gdx
import com.snake.game.ecs.component.PositionComponent
import com.snake.game.ecs.entity.Entity
import com.snake.game.ecs.system.DynamicsSystem

object SnakeECSEngine: ECSEngine(
        DynamicsSystem
) {
    init {

        // Testing
        var test = Entity(entityManager);
        test.addComponent(PositionComponent());
        Gdx.app.debug("test", "testing update");
        update(30f);
    }
}