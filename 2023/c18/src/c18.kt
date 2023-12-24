import java.io.File
import kotlin.math.abs

enum class Direction { UP, DOWN, LEFT, RIGHT }
data class Move(val direction: Direction, val length: Int) {
    companion object {
        private fun parseOne1(line: String): Move {
            val data = line.split(" ")
            val direction = when (data[0]) {
                "U" -> Direction.UP
                "D" -> Direction.DOWN
                "L" -> Direction.LEFT
                "R" -> Direction.RIGHT
                else -> error("Should not happen")
            }
            val length = data[1].toInt()
            return Move(direction, length)
        }
        fun parse1(lines: List<String>) = lines.map { parseOne1(it) }

        private fun parseOne2(line: String): Move {
            val data = line.split(" ")[2].drop(2).dropLast(1)
            val length = data.take(5).toInt(16)
            val direction = when (data.takeLast(1)) {
                "0" -> Direction.RIGHT
                "1" -> Direction.DOWN
                "2" -> Direction.LEFT
                "3" -> Direction.UP
                else -> error("Should not happen")
            }
            return Move(direction, length)
        }
        fun parse2(lines: List<String>) = lines.map { parseOne2(it) }
    }
}

data class Pos(val x: Int, val y: Int)
data class Edge(val from: Pos, val to: Pos, val direction: Direction, val length: Int)
class Lagoon {
    private val edges = mutableListOf<Edge>()
    private var actualPos = Pos(0, 0)
    fun addEdge(move: Move) {
        val newPos = when (move.direction) {
            Direction.UP -> Pos(actualPos.x - move.length, actualPos.y)
            Direction.DOWN -> Pos(actualPos.x + move.length, actualPos.y)
            Direction.LEFT -> Pos(actualPos.x, actualPos.y - move.length)
            Direction.RIGHT -> Pos(actualPos.x, actualPos.y + move.length)
        }
        edges.add(Edge(actualPos, newPos, move.direction, move.length))
        actualPos = newPos
    }

    private fun perimeter(): Long = edges.sumOf { it.length.toLong() }

    // https://en.wikipedia.org/wiki/Shoelace_formula, with correction to add trench area
    fun area(): Long {
        var sum = 0L
        for (edge in edges) sum += (edge.from.y + edge.to.y).toLong() * (edge.from.x - edge.to.x).toLong()
        return (abs(sum) / 2) + (perimeter() / 2) + 1
    }
}

fun main() {
    val file = File("inputs/c18")
    val lines = file.readLines()

    // part 1
    val moves1 = Move.parse1(lines)
    val lagoon1 = Lagoon()
    for (move in moves1) lagoon1.addEdge(move)
    println(lagoon1.area())

    // part 2
    val moves2 = Move.parse2(lines)
    val lagoon2 = Lagoon()
    for (move in moves2) lagoon2.addEdge(move)
    println(lagoon2.area())
}