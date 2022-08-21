package com.temnenkov.maze

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DrawMazeKtTest {
    @Test
    fun noPasses() {
        val maze: Maze2D = Maze2DImpl(6, 5)
        val display = drawMaze(maze)
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
            display
        )
    }
}
