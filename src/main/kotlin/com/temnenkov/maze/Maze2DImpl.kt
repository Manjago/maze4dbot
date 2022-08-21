package com.temnenkov.maze

import java.lang.IllegalArgumentException
import kotlin.experimental.and
import kotlin.experimental.or

class Maze2DImpl(override val width: Int, override val heigth: Int) : Maze2D {

    private val data = Array(heigth) { ByteArray(width) }

    override fun linkToNorth(x: Int, y: Int): Boolean = link(x, y, x, y + 1, NORTH)

    override fun linkToEast(x: Int, y: Int): Boolean = link(x, y, x + 1, y, EAST)

    override fun passableToNorth(x: Int, y: Int): Boolean = passableTo(x, y, NORTH)

    override fun passableToSouth(x: Int, y: Int): Boolean = passableTo(x, y, SOUTH)

    override fun passableToWest(x: Int, y: Int): Boolean = passableTo(x, y, WEST)

    override fun passableToEast(x: Int, y: Int): Boolean = passableTo(x, y, EAST)

    private fun link(x: Int, y: Int, otherX: Int, otherY: Int, direction: Byte): Boolean {
        if (isValid(otherX, otherY)) {
            data[y][x] = data[y][x] or direction
            data[otherY][x] = data[otherY][x] or inv(direction)
            return true
        } else {
            return false
        }
    }

    private fun passableTo(x: Int, y: Int, direction: Byte): Boolean =
        (data[y][x] and direction) != ZERO

    private fun isValid(x: Int, y: Int): Boolean =
        (x in (0 until width)) && (y in (0 until heigth))

    private fun inv(b: Byte) = when (b) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        WEST -> EAST
        EAST -> WEST
        else -> throw IllegalArgumentException("$b")
    }

    companion object {
        private val NORTH: Byte = 1
        private val SOUTH: Byte = 2
        private val WEST: Byte = 4
        private val EAST: Byte = 8
        private val ZERO: Byte = 0
    }
}
