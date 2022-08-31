package com.temnenkov.maze.builder

import com.temnenkov.maze.MutableMaze2D

interface MazeBuilder {
    fun build(maze: MutableMaze2D)
}
