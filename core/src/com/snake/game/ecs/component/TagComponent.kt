package com.snake.game.ecs.component

import org.json.JSONObject

class TagComponent(): Component(ComponentType.Tag) {
    var tag: String = ""

    override fun updateFromJSON(data: JSONObject) {
        tag = data.getString("tag")
    }
}