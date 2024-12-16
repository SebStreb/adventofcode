import java.io.File
import java.util.*

enum class Cell { OPEN, WALL }
enum class Facing {
    NORTH, SOUTH, WEST, EAST;

    fun turnLeft() = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }

    fun turnRight() = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
    }
}

data class Position(val x: Int, val y: Int) {
    fun next(facing: Facing) = when (facing) {
        Facing.NORTH -> Position(x, y - 1)
        Facing.SOUTH -> Position(x, y + 1)
        Facing.WEST -> Position(x - 1, y)
        Facing.EAST -> Position(x + 1, y)
    }
}

data class State(val position: Position, val facing: Facing) {
    fun next(map: Map<Position, Cell>, visited: Set<State>) = mutableListOf(
        Option(State(position.next(facing), facing), 1),
        Option(State(position, facing.turnLeft()), 1000),
        Option(State(position, facing.turnRight()), 1000),
    ).filter { map[it.state.position] != Cell.WALL }.filter { !visited.contains(it.state) }
}

data class Option(val state: State, val cost: Int)

data class Reindeer(val state: State, val path: List<State>, val score: Int) {
    fun takeOptions(options: List<Option>) = options.map { Reindeer(it.state, path + it.state, score + it.cost) }
}

class Maze(private val map: Map<Position, Cell>, private val start: Position, private val end: Position) {

    fun shortestPath(): Int {
        val firstState = State(start, Facing.EAST)

        val open = PriorityQueue<Reindeer>(compareBy { it.score })
        open.add(Reindeer(firstState, listOf(firstState), 0))

        val visited = mutableSetOf<State>()

        while (open.isNotEmpty()) {
            val reindeer = open.poll()
            if (reindeer.state.position == end) return reindeer.score
            visited.add(reindeer.state)
            val options = reindeer.state.next(map, visited)
            open.addAll(reindeer.takeOptions(options))
        }

        return Int.MAX_VALUE
    }

    fun tilesInBestPaths(): Int {
        val shortestPath = shortestPath()
        val tiles = mutableSetOf<Position>()

        val firstState = State(start, Facing.EAST)

        val open = PriorityQueue<Reindeer>(compareBy { it.score })
        open.add(Reindeer(firstState, listOf(firstState), 0))

        val visited = mutableSetOf<State>()

        while (open.isNotEmpty()) {
            val reindeer = open.poll()
            if (reindeer.state.position == end) {
                tiles.addAll(reindeer.path.map { it.position })
            } else {
                visited.add(reindeer.state)
                val options = reindeer.state.next(map, visited)
                val paths = reindeer.takeOptions(options).filter { it.score <= shortestPath }
                open.addAll(paths)
            }
        }

        return tiles.size
    }

    companion object {
        fun parse(lines: List<String>): Maze {
            val map = mutableMapOf<Position, Cell>()
            var start = Position(0, 0)
            var end = Position(0, 0)
            for (y in lines.indices) {
                for (x in lines[y].indices) {
                    val cell = when (lines[y][x]) {
                        '.' -> Cell.OPEN
                        '#' -> Cell.WALL
                        'S' -> Cell.OPEN.also { start = Position(x, y) }
                        'E' -> Cell.OPEN.also { end = Position(x, y) }
                        else -> throw IllegalArgumentException("Unknown cell: ${lines[y][x]}")
                    }
                    map[Position(x, y)] = cell
                }
            }
            return Maze(map, start, end)
        }
    }
}

fun main() {
    val file = File("inputs/test.txt")
    val lines = file.readLines()

    // part 1
    val maze = Maze.parse(lines)
    println(maze.shortestPath())

    // part 2
    println(maze.tilesInBestPaths())
}