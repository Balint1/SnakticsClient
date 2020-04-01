package com.snake.game.ecs.component

enum class ComponentTypeOperator { AND, OR }

class ComponentTypeTree(var node: Node, var leftChild: ComponentTypeTree?, var rightChild: ComponentTypeTree?) {
    constructor(value: ComponentType) :
            this(Node(value), null, null)

    constructor(operator: ComponentTypeOperator, left: ComponentType, right: ComponentType) :
            this(Node(operator), ComponentTypeTree(left), ComponentTypeTree(right))

    infix fun and(componentTypeTree: ComponentTypeTree): ComponentTypeTree {
        return ComponentTypeTree(Node(ComponentTypeOperator.AND), this, componentTypeTree)
    }
    infix fun or(componentTypeTree: ComponentTypeTree): ComponentTypeTree {
        return ComponentTypeTree(Node(ComponentTypeOperator.OR), this, componentTypeTree)
    }
    infix fun and(componentType: ComponentType): ComponentTypeTree {
        return this and ComponentTypeTree(componentType)
    }
    infix fun or(componentType: ComponentType): ComponentTypeTree {
        return this or ComponentTypeTree(componentType)
    }
}

class Node(
    var operator: ComponentTypeOperator?,
    var type: ComponentType?
) {

    constructor(operator: ComponentTypeOperator?) : this(operator, null)
    constructor(type: ComponentType?) : this(null, type)
}