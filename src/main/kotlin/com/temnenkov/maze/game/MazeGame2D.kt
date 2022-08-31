package com.temnenkov.maze.game

interface MazeGame2D {

    enum class DIRECTION {
        NORTH, SOUTH, WEST, EAST
    }

    enum class State {
        NEED_INIT, WAIT_TURN, FINISHED
    }

    fun state(): State
    fun initialize(width: Int, height: Int)
    fun turnRight()
    fun turnLeft()
    fun stepForward(): Boolean
}
