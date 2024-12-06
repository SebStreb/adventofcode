import java.io.File

enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    fun turn() = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }
}

data class Position(val x: Int, val y: Int) {
    fun next(direction: Direction) = when (direction) {
        Direction.UP -> Position(x - 1, y)
        Direction.DOWN -> Position(x + 1, y)
        Direction.LEFT -> Position(x, y - 1)
        Direction.RIGHT -> Position(x, y + 1)
    }

    fun isValid(size: Int) = x in 0 until size && y in 0 until size
}
data class DirectedPosition(val position: Position, val facing: Direction)

enum class Cell {
    EMPTY, WALL;
}

open class Map(val map: List<List<Cell>>, private val start: DirectedPosition) {
    var guard = start
    val size = map.size
    val visited = mutableSetOf(guard.position)

    private fun step(): Boolean {
        val nextPosition = guard.position.next(guard.facing)

        if (!nextPosition.isValid(size)) return false

        guard = if (map[nextPosition.x][nextPosition.y] == Cell.WALL) {
            guard.copy(facing = guard.facing.turn())
        } else {
            visited.add(nextPosition)
            guard.copy(position = nextPosition)
        }

        return true
    }

    fun part1() {
        do {
            val ok = step()
        } while (ok)
    }

    fun part2(): Int {
        var loops = 0
        val newMap = map.map { it.toMutableList() }
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (map[i][j] == Cell.EMPTY) {
                    newMap[i][j] = Cell.WALL
                    val map2 = Map2(newMap, start)
                    if (map2.isLoop()) loops++
                    newMap[i][j] = Cell.EMPTY
                }
            }
        }
        return loops
    }

    companion object {
        fun parse(lines: List<String>): Map {
            lateinit var startingPosition: DirectedPosition
            val map = lines.mapIndexed { x, line ->
                line.mapIndexed { y, cell ->
                    when (cell) {
                        '.' -> Cell.EMPTY
                        '#' -> Cell.WALL
                        '^' -> {
                            startingPosition = DirectedPosition(Position(x, y), Direction.UP)
                            Cell.EMPTY
                        }

                        else -> throw IllegalArgumentException("Unknown cell: $cell")
                    }
                }
            }
            return Map(map, startingPosition)
        }
    }
}

class Map2(map: List<List<Cell>>, guard: DirectedPosition): Map(map, guard) {
    private val round = mutableSetOf(guard)

    private fun step(): DirectedPosition? {
        val nextPosition = guard.position.next(guard.facing)

        if (!nextPosition.isValid(size)) return null

        return if (map[nextPosition.x][nextPosition.y] == Cell.WALL) {
            guard.copy(facing = guard.facing.turn())
        } else {
            guard.copy(position = nextPosition)
        }
    }

    fun isLoop(): Boolean {
        do {
            val next = step() ?: return false
            if (round.contains(next)) return true
            round.add(next)
            guard = next
        } while (true)
    }
}

fun main() {
    val file = File("inputs/d06.txt")
    val lines = file.readLines()

    // part 1
    val map = Map.parse(lines)
    map.part1()
    println(map.visited.size)

    // part 2
    val loops = map.part2()
    println(loops)
}