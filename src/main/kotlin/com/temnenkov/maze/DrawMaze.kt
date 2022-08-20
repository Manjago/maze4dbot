package com.temnenkov.maze

import java.lang.StringBuilder

fun drawMaze(maze: Maze2D): String {
    val cross = '+'
    val hor = '-'
    val ver = '|'
    val space = ' '
    val ln = '\n'

    // +-+-+-+-+ +-+
    val sb = StringBuilder()
    sb.append(cross)
    for (x in 0 until maze.width) {
        if (maze.passableToNorth(x, maze.heigth - 1)) {
            sb.append(space)
        } else {
            sb.append(hor)
        }
    }
    sb.append(ln)

    for (y in maze.heigth - 1 downTo 0) {
        for (x in 0 until maze.width) {
            when {
                x == 0 -> if (maze.passableToWest(x, y)) {
                    sb.append(space)
                } else {
                    sb.append(ver)
                }
                x % 2 == 1 -> sb.append(space)
                else -> if (maze.passableToEast(x, y)) {
                    sb.append(space)
                } else {
                    sb.append(ver)
                }
            }
        }
        sb.append(ln)

        for (x in 0 until maze.width) {
            when {
                x % 2 == 0 -> sb.append(cross)
                else -> if (maze.passableToSouth(x, y)) {
                    sb.append(space)
                } else {
                    sb.append(hor)
                }
            }
        }
        sb.append(ln)
    }

    return sb.toString()
}
