package com.temnenkov.maze.game

import com.temnenkov.maze.builder.Sidewalker
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class MazeGame2DImplTest {
    @Test
    internal fun sidewalker() {
        val random = Random(1L)
        val sidewalker = Sidewalker(random)
        val game = MazeGame2DImpl(sidewalker, random)
    }
}
