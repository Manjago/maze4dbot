package com.temnenkov.maze

interface MazeMaker {
    fun MazeCell.makePassableTo(dest: MazeCell)
}
