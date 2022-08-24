package com.temnenkov.maze.builder

import com.temnenkov.maze.MutableMaze2DImpl
import com.temnenkov.maze.drawMaze
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

internal class SidewalkerTest {
    @Test
    internal fun build() {
        val sidewalker = Sidewalker(Random(1L))
        val maze2D = MutableMaze2DImpl(5, 5)
        sidewalker.build(maze2D)
        assertEquals(
            """+-+-+-+-+-+
|         |
+-+-+-+-+ +
|         |
+-+-+ +-+ +
|     |   |
+-+ + + + +
|   | | | |
+ +-+ +-+ +
| |   |   |
+-+-+-+-+-+
""",
            drawMaze(maze2D)
        )
    }
}
