import java.io.File
import java.util.*

data class Position(val x: Int, val y: Int) {
    val heuristic = x + y
    val neighbors get() = setOf(
        Position(x - 1, y),
        Position(x + 1, y),
        Position(x, y - 1),
        Position(x, y + 1),
    )

    override fun toString() = "$x,$y"
    constructor(line: String) : this(line.split(",")[0].toInt(), line.split(",")[1].toInt())
}

data class MemorySpace(val fallingBytes: List<Position>) {
    private val size = 71
    private val corrupted = mutableSetOf<Position>()
    private val start = Position(0, 0)
    private val end = Position(size - 1, size - 1)

    private fun Position.isValid() = this.x in 0 until size && this.y in 0 until size && !corrupted.contains(this)

    // https://en.wikipedia.org/wiki/A*_search_algorithm
    private fun aStar(): Int {
        val cost = mutableMapOf(start to 0)
        val estimated = mutableMapOf(start to start.heuristic)
        val open = PriorityQueue<Position>(compareBy { estimated[it] ?: Int.MAX_VALUE })
        open.add(start)

        while (open.isNotEmpty()) {
            val current = open.remove()
            val currentCost = cost[current] ?: Int.MAX_VALUE
            if (current == end) return currentCost

            for (next in current.neighbors.filter { it.isValid() }) {
                val nextCost = cost[next] ?: Int.MAX_VALUE
                val possibleCost = currentCost + 1
                if (possibleCost < nextCost) {
                    cost[next] = possibleCost
                    estimated[next] = possibleCost + next.heuristic
                    if (next !in open) open.add(next)
                }
            }
        }
        return -1
    }

    fun part1(): Int {
        corrupted.addAll(fallingBytes.take(1024))
        val result = aStar()
        corrupted.clear()
        return result
    }

    fun part2(): Position {
        for (byte in fallingBytes) {
            corrupted += byte
            if (aStar() == -1) return byte
        }
        throw Exception("Can always find a route")
    }

    companion object {
        fun parse(lines: List<String>) = MemorySpace(lines.map { Position(it) })
    }
}

fun main() {
    val file = File("inputs/d18.txt")
    val lines = file.readLines()
    val memorySpace = MemorySpace.parse(lines)

    // part 1
    println(memorySpace.part1())

    // part 2
    println(memorySpace.part2())
}