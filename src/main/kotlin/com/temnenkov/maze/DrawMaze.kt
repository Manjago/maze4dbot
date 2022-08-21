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
        sb.append(hor)
        sb.append(cross)
    }
    sb.append(ln)

    for (y in maze.heigth - 1 downTo 0) {
        sb.append(ver)
        for (x in 0 until maze.width) {
            sb.append(space)
            if (maze.passableToEast(x, y)) {
                sb.append(space)
            } else {
                sb.append(ver)
            }
        }
        sb.append(ln)

        sb.append(cross)
        for (x in 0 until maze.width) {
            if (maze.passableToSouth(x, y)) {
                sb.append(space)
            } else {
                sb.append(hor)
            }
            sb.append(cross)
        }
        sb.append(ln)
    }

    return sb.toString()
}
