import java.io.File

enum class Direction(private val char: Char) {
    LEFT('<'), RIGHT('>'), UP('^'), DOWN('v');

    val isHorizontal get() = this == LEFT || this == RIGHT

    val vector get() = when (this) {
        LEFT -> Position(-1, 0)
        RIGHT -> Position(1, 0)
        UP -> Position(0, -1)
        DOWN -> Position(0, 1)
    }

    override fun toString() = char.toString()

    companion object {
        private fun fromChar(char: Char) = when (char) {
            '<' -> LEFT
            '>' -> RIGHT
            '^' -> UP
            'v' -> DOWN
            else -> throw IllegalArgumentException("Unknown direction: $char")
        }

        fun parse(lines: List<String>) = lines.flatMap { line -> line.map { fromChar(it) } }
    }
}

enum class Cell(private val char: Char) {
    WALL('#'), EMPTY('.'), BOX('O'), ROBOT('@'), BOX_LEFT('['), BOX_RIGHT(']');

    val moveable get() = this == ROBOT || this == BOX || this == BOX_LEFT || this == BOX_RIGHT

    override fun toString() = char.toString()

    companion object {
        fun fromChar(char: Char) = when (char) {
            '#' -> WALL
            '.' -> EMPTY
            'O' -> BOX
            '@' -> ROBOT
            else -> throw IllegalArgumentException("Unknown cell: $char")
        }
    }
}

data class Position(val x: Int, val y: Int) {
    val coordinate = 100 * y + x

    override fun toString() = "($x, $y)"

    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}

class Warehouse1(
    private val cells: MutableMap<Position, Cell>,
    private val movements: List<Direction>,
    private var robot: Position,
    private val size: Int
) {
    private fun apply(movement: Direction) {
        val vec = movement.vector
        var pos = robot + vec
        while (cells[pos]!!.moveable) pos += vec
        if (cells[pos] == Cell.WALL) return
        do {
            val toMove = pos - vec
            cells[pos] = cells[toMove]!!
            pos -= vec
        } while (toMove != robot)
        cells[robot] = Cell.EMPTY
        robot += vec
    }

    fun solve(): Int {
        for (movement in movements) apply(movement)
        return cells.filter { it.value == Cell.BOX }.keys.sumOf { it.coordinate }
    }

    override fun toString(): String {
        var str = ""
        for (y in 0 until size) {
            for (x in 0 until size) {
                str += cells[Position(x, y)]
            }
            str += "\n"
        }
        return str
    }

    companion object {
        fun parse(lines: List<String>): Warehouse1 {
            val cells = mutableMapOf<Position, Cell>()
            var robot = Position(0, 0)

            var lineIndex = 0
            while (lines[lineIndex].isNotEmpty()) {
                val y = lineIndex
                for ((x, char) in lines[lineIndex].withIndex()) {
                    val pos = Position(x, y)
                    val cell = Cell.fromChar(char)
                    cells[pos] = cell
                    if (cell == Cell.ROBOT) robot = pos
                }
                lineIndex++
            }
            val size = lineIndex

            val movements = Direction.parse(lines.drop(lineIndex + 1))

            return Warehouse1(cells, movements, robot, size)
        }
    }
}

class Warehouse2(
    private val cells: MutableMap<Position, Cell>,
    private val movements: List<Direction>,
    private var robot: Position,
    private val size: Int
) {
    private fun canMoveHorizontal(pos: Position, direction: Direction): Boolean {
        val next = pos + direction.vector
        return when (cells[next]) {
            Cell.WALL -> false
            Cell.BOX_LEFT -> canMoveHorizontal(next, direction)
            Cell.BOX_RIGHT -> canMoveHorizontal(next, direction)
            Cell.EMPTY -> true
            else -> throw IllegalStateException("Unknown cell: $next, ${cells[next]}")
        }
    }

    private fun canMoveVertical(pos: Position, direction: Direction): Boolean {
        val next = pos + direction.vector
        return when (cells[next]) {
            Cell.WALL -> false
            Cell.BOX_LEFT -> canMoveVertical(next, direction) && canMoveVertical(next + Position(1, 0), direction)
            Cell.BOX_RIGHT -> canMoveVertical(next, direction) && canMoveVertical(next + Position(-1, 0), direction)
            Cell.EMPTY -> true
            else -> throw IllegalStateException("Unknown cell: $next, ${cells[next]}")
        }
    }

    private fun moveHorizontal(pos: Position, direction: Direction) {
        val next = pos + direction.vector
        when (cells[next]) {
            Cell.BOX_LEFT -> moveHorizontal(next, direction)
            Cell.BOX_RIGHT -> moveHorizontal(next, direction)
            else -> {}
        }
        cells[next] = cells[pos]!!
        cells[pos] = Cell.EMPTY
    }

    private fun moveVertical(pos: Position, direction: Direction) {
        val next = pos + direction.vector
        when (cells[next]) {
            Cell.BOX_LEFT -> {
                moveVertical(next, direction)
                moveVertical(next + Position(1, 0), direction)
            }
            Cell.BOX_RIGHT -> {
                moveVertical(next, direction)
                moveVertical(next + Position(-1, 0), direction)
            }
            else -> {}
        }
        cells[next] = cells[pos]!!
        cells[pos] = Cell.EMPTY
    }

    private fun applyHorizontal(pos: Position, direction: Direction): Boolean {
        if (!canMoveHorizontal(pos, direction)) return false
        moveHorizontal(pos, direction)
        return true
    }

    private fun applyVertical(pos: Position, direction: Direction): Boolean {
        if (!canMoveVertical(pos, direction)) return false
        moveVertical(pos, direction)
        return true
    }

    fun solve(): Int {
        for (movement in movements) {
            val moved = if (movement.isHorizontal) applyHorizontal(robot, movement) else applyVertical(robot, movement)
            if (moved) robot += movement.vector
        }
        return cells.filter { it.value == Cell.BOX_LEFT }.keys.sumOf { it.coordinate }
    }

    override fun toString(): String {
        var str = ""
        for (y in 0 until size) {
            for (x in 0 until size * 2) {
                str += cells[Position(x, y)]
            }
            str += "\n"
        }
        return str
    }

    companion object {
        fun parse(lines: List<String>): Warehouse2 {
            val cells = mutableMapOf<Position, Cell>()
            var robot = Position(0, 0)

            var lineIndex = 0
            while (lines[lineIndex].isNotEmpty()) {
                val y = lineIndex
                var x = 0
                for (char in lines[lineIndex]) {
                    when (char) {
                        '#' -> {
                            cells[Position(x++, y)] = Cell.WALL
                            cells[Position(x++, y)] = Cell.WALL
                        }
                        '.' -> {
                            cells[Position(x++, y)] = Cell.EMPTY
                            cells[Position(x++, y)] = Cell.EMPTY
                        }
                        'O' -> {
                            cells[Position(x++, y)] = Cell.BOX_LEFT
                            cells[Position(x++, y)] = Cell.BOX_RIGHT
                        }
                        '@' -> {
                            robot = Position(x, y)
                            cells[Position(x++, y)] = Cell.ROBOT
                            cells[Position(x++, y)] = Cell.EMPTY
                        }
                    }
                }
                lineIndex++
            }
            val size = lineIndex

            val movements = Direction.parse(lines.drop(lineIndex + 1))

            return Warehouse2(cells, movements, robot, size)
        }
    }
}

fun main() {
    val file = File("inputs/d15.txt")
    val lines = file.readLines()

    // part 1
    val warehouse1 = Warehouse1.parse(lines)
    println(warehouse1.solve())

    // part 2
    val warehouse2 = Warehouse2.parse(lines)
    println(warehouse2.solve())
}