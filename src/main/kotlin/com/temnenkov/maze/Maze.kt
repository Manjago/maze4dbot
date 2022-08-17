package com.temnenkov.maze

interface Maze {
    fun MazeCell.passableTo(dest: MazeCell): Boolean
    val dimensions: List<Int>
}
