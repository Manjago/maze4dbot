package com.temnenkov.maze.builder

import com.temnenkov.maze.MutableMaze2D
import kotlin.random.Random

class Sidewalker(private val random: Random) : MazeBuilder {

    override fun build(maze: MutableMaze2D) {
        for (x in 0 until maze.width) {
            for (y in 0 until maze.heigth) {
                when {
                    (y == maze.heigth - 1) && (x == maze.width - 1) -> return
                    y == maze.heigth - 1 -> maze.linkToEast(x, y)
                    x == maze.width - 1 -> maze.linkToNorth(x, y)
                    else -> {
                        when (random.nextInt(2)) {
                            0 -> maze.linkToNorth(x, y)
                            1 -> maze.linkToEast(x, y)
                        }
                    }
                }
            }
        }
    }
}
