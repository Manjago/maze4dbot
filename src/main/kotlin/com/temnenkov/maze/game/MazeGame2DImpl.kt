package com.temnenkov.maze.game

import com.temnenkov.maze.MutableMaze2D
import com.temnenkov.maze.MutableMaze2DImpl
import com.temnenkov.maze.builder.MazeBuilder
import com.temnenkov.maze.game.MazeGame2DImpl.Direction.EAST
import com.temnenkov.maze.game.MazeGame2DImpl.Direction.NORD
import com.temnenkov.maze.game.MazeGame2DImpl.Direction.SOUTH
import com.temnenkov.maze.game.MazeGame2DImpl.Direction.WEST
import kotlin.random.Random

class MazeGame2DImpl(
    private val mazeBuilder: MazeBuilder,
    private val random: Random
) : MazeGame2D {

    private var state: MazeGame2D.State = MazeGame2D.State.NEED_INIT
    private lateinit var maze: MutableMaze2D
    private var x = -1
    private var y = -1
    private lateinit var direction: Direction

    override fun state(): MazeGame2D.State = state

    override fun initialize(width: Int, height: Int) {
        check(state == MazeGame2D.State.NEED_INIT)

        maze = MutableMaze2DImpl(width, height)
        mazeBuilder.build(maze)
        x = width - 1
        y = height - 1
        direction = Direction.values[random.nextInt(Direction.values.size)]

        state = MazeGame2D.State.WAIT_TURN
    }

    override fun turnRight() {
        check(state == MazeGame2D.State.WAIT_TURN)

        direction = when (direction) {
            NORD -> EAST
            WEST -> NORD
            SOUTH -> WEST
            EAST -> SOUTH
        }
    }

    override fun turnLeft() {
        check(state == MazeGame2D.State.WAIT_TURN)

        direction = when (direction) {
            NORD -> WEST
            WEST -> SOUTH
            SOUTH -> EAST
            EAST -> NORD
        }
    }

    override fun stepForward(): Boolean {
        check(state == MazeGame2D.State.WAIT_TURN)

        return when (direction) {
            NORD -> if (maze.passableToNorth(x, y)) {
                y++
                updateState()
                true
            } else {
                false
            }

            WEST -> if (maze.passableToWest(x, y)) {
                x--
                updateState()
                true
            } else {
                false
            }

            SOUTH -> if (maze.passableToSouth(x, y)) {
                y--
                updateState()
                true
            } else {
                false
            }

            EAST -> if (maze.passableToSouth(x, y)) {
                x++
                updateState()
                true
            } else {
                false
            }
        }
    }

    private fun updateState() {
        check(state == MazeGame2D.State.WAIT_TURN)

        if (x == 0 && y == 0) {
            state = MazeGame2D.State.FINISHED
        }
    }

    private enum class Direction {
        NORD, WEST, SOUTH, EAST;

        companion object {
            val values: Array<Direction> = values()
        }
    }
}
