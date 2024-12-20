import java.io.File
import java.util.*
import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    val next get() = listOf(
        Point(x - 1, y),
        Point(x + 1, y),
        Point(x, y - 1),
        Point(x, y + 1),
    )

    fun distanceTo(that: Point) = abs(this.x - that.x) + abs(this.y - that.y)
}

data class RaceMap(val walls: Set<Point>, val start: Point, val end: Point, val size: Int) {

    private val Point.heuristic get() = distanceTo(end)
    private val Point.isValid get() = this.x in 0 until size && this.y in 0 until size && this !in walls

    private fun aStar(): List<Point> {
        val cameFrom = mutableMapOf<Point, Point>()
        val cost = mutableMapOf(start to 0)
        val estimated = mutableMapOf(start to start.heuristic)
        val open = PriorityQueue<Point>(compareBy { estimated[it] ?: Int.MAX_VALUE })
        open.add(start)

        while (open.isNotEmpty()) {
            val current = open.remove()
            val currentCost = cost[current] ?: Int.MAX_VALUE
            if (current == end) {
                val path = mutableListOf(current)
                var point = current
                while (point in cameFrom) {
                    point = cameFrom[point]
                    path.addFirst(point)
                }
                return path
            }

            for (neighbor in current.next.filter { it.isValid }) {
                val nextCost = cost[neighbor] ?: Int.MAX_VALUE
                val possibleCost = currentCost + 1
                if (possibleCost < nextCost) {
                    cameFrom[neighbor] = current
                    cost[neighbor] = possibleCost
                    estimated[neighbor] = possibleCost + neighbor.heuristic
                    if (neighbor !in open) open.add(neighbor)
                }
            }
        }
        error("No path found")
    }

    fun part1(test: Boolean): Int {
        val timeToBeat = aStar().size
        val minimumSave = if (test) 0 else 99
        val saves = mutableMapOf<Int, MutableSet<Point>>()
        for (x in 1 until (size - 1)) {
            for (y in 1 until (size - 1)) {
                val point = Point(x, y)
                if (point in walls) {
                    val cheat = RaceMap(walls - point, start, end, size)
                    val time = cheat.aStar().size
                    if (time + minimumSave < timeToBeat) {
                        if (!saves.containsKey(time)) saves[time] = mutableSetOf()
                        saves[time]!!.add(point)
                    }
                }
            }
        }
        return saves.map { it.value.size }.sum()
    }

    fun part2(test: Boolean): Int {
        var count = 0

        val maximumCheat = 20
        val minimumSave = if (test) 49 else 99
        val path = aStar()

        for ((t1, p1) in path.withIndex()) {
            for ((t2, p2) in path.withIndex()) {
                val dist = p1.distanceTo(p2)
                val save = t2 - t1 - dist
                if (dist <= maximumCheat && save > minimumSave) count++
            }
        }

        return count
    }

    companion object {
        fun parse(lines: List<String>): RaceMap {
            val walls = mutableSetOf<Point>()
            var start = Point(0, 0)
            var end = Point(0, 0)
            val size = lines.size

            for ((y, line) in lines.withIndex()) {
                for ((x, char) in line.withIndex()) {
                    val point = Point(x, y)
                    when (char) {
                        '#' -> walls += point
                        'S' -> start = point
                        'E' -> end = point
                    }
                }
            }

            return RaceMap(walls, start, end, size)
        }
    }
}

fun main() {
    val test = false
    val file = File(if (test) "inputs/test.txt" else "inputs/d20.txt")
    val lines = file.readLines()
    val raceMap = RaceMap.parse(lines)

    // part 1
    println(raceMap.part1(test))

    // part 2
    println(raceMap.part2(test))
}