package com.temnenkov.maze

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DrawMazeKtTest {
    @Test
    fun noPasses() {
        val maze: Maze2D = Maze2DImpl(6, 5)
        assertEquals(
            """+-+-+-+-+-+-+
| | | | | | |
+-+-+-+-+-+-+
| | | | | | |
+-+-+-+-+-+-+
| | | | | | |
+-+-+-+-+-+-+
| | | | | | |
+-+-+-+-+-+-+
| | | | | | |
+-+-+-+-+-+-+
""",
            drawMaze(maze)
        )
    }

    @Test
    internal fun twoPasses() {
        val maze: Maze2D = Maze2DImpl(3, 3)
        maze.linkToNorth(0, 0)
        maze.linkToEast(0, 1)
        assertEquals(
            """+-+-+-+
| | | |
+-+-+-+
|   | |
+ +-+-+
| | | |
+-+-+-+
""",
            drawMaze(maze)
        )
    }
}
