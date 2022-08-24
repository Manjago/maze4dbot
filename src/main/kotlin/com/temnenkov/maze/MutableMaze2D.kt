package com.temnenkov.maze

interface MutableMaze2D : Maze2D {
    fun linkToNorth(x: Int, y: Int): Boolean
    fun linkToEast(x: Int, y: Int): Boolean
}
