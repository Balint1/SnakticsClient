package com.snake.game.ecs.component

import org.json.JSONObject
import javax.swing.text.html.parser.Entity

class TagComponent(): Component(ComponentType.Tag) {
    enum class EntityTagType(val typeString: String) {
        SnakeHead("snakeHead"),
        SnakeBody("snakeBody"),
        Food("food"),
        Powerup("powerup")
    }

    var tag: EntityTagType? = null

    override fun updateFromJSON(data: JSONObject) {
        val tagValue = data.getString("tag")
        for(tag in EntityTagType.values()) {
            if(tag.typeString == tagValue) {
                this.tag = tag
                break
            }
        }
    }
}