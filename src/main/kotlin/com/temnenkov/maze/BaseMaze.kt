package com.temnenkov.maze

class BaseMaze(override val dimensions: List<Int>) : Maze, MazeMaker {

    private val edges = mutableMapOf<MazeCell, MutableSet<MazeCell>>()

    override fun MazeCell.passableTo(dest: MazeCell): Boolean = edges[this]?.contains(dest) == true

    override fun MazeCell.makePassableTo(dest: MazeCell) {
        val destinations = edges[this] ?: mutableSetOf()
        destinations.add(dest)
        edges[this] = destinations
    }
}
