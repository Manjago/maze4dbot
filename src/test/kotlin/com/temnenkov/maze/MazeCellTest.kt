package com.temnenkov.maze

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MazeCellTest {

    @Test
    fun reallyEqual() {
        val cell1 = MazeCell(listOf(0, 1))
        val cell2 = MazeCell(listOf(0, 1))
        assertTrue(cell1 == cell2)
    }

    @Test
    fun reallyNotEqual() {
        val cell1 = MazeCell(listOf(0, 0))
        val cell2 = MazeCell(listOf(0, 1))
        assertTrue(cell1 != cell2)
    }

    @Test
    fun totallyNotEqual() {
        val cell1 = MazeCell(listOf(0))
        val cell2 = MazeCell(listOf(0, 0))
        assertTrue(cell1 != cell2)
    }
}
