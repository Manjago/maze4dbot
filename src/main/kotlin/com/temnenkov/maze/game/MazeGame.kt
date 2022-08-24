package com.temnenkov.maze.game

import com.temnenkov.maze.Maze2D

class MazeGame(private val maze2D: Maze2D) {

    private enum class DIRECTION {
        NORTH, SOUTH, WEST, EAST
    }
}
