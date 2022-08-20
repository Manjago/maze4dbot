package com.temnenkov.maze

import kotlin.experimental.or

class Maze2DImpl(override val width: Int, override val heigth: Int) : Maze2D {

    private val data = Array(heigth) { ByteArray(width) }

    override fun linkToNorth(x: Int, y: Int) {
        data[y][x] = data[y][x] or NORTH
        if ((y + 1) <= heigth - 1) {
            data[y + 1][x] = data[y + 1][x] or SOUTH
        }
    }

    override fun linkToEast(x: Int, y: Int) {
        data[y][x] = data[y][x] or EAST
        if ((x + 1) <= width - 1) {
            data[y][x + 1] = data[y][x + 1] or WEST
        }
    }

    override fun passableToNorth(x: Int, y: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun passableToSouth(x: Int, y: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun passableToWest(x: Int, y: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun passableToEast(x: Int, y: Int): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private val NORTH: Byte = 1
        private val SOUTH: Byte = 2
        private val WEST: Byte = 4
        private val EAST: Byte = 8
    }
}
