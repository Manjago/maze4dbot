package com.temnenkov.maze

interface Maze2D {
    val width: Int
    val heigth: Int
    fun linkToNorth(x: Int, y: Int)
    fun linkToEast(x: Int, y: Int)
    fun passableToNorth(x: Int, y: Int): Boolean
    fun passableToSouth(x: Int, y: Int): Boolean
    fun passableToWest(x: Int, y: Int): Boolean
    fun passableToEast(x: Int, y: Int): Boolean
}
