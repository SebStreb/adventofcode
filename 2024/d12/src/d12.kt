import java.io.File

data class Point(val x: Int, val y: Int) {
    val neighbors get() = listOf(
        Point(x - 1, y),
        Point(x + 1, y),
        Point(x, y - 1),
        Point(x, y + 1),
    )
}

data class Region(val plant: Char, val points: MutableSet<Point>) {
    val area get() = points.size
    val perimeter get() =
        points.map { it.neighbors.filter { neighbor -> !points.contains(neighbor) } }.flatten().size

    val sides: Int get() {
        var corners = 0
        for (point in points) {
            val topLeft = Point(point.x - 1, point.y - 1) in points
            val top = Point(point.x, point.y - 1) in points
            val topRight = Point(point.x + 1, point.y - 1) in points
            val left = Point(point.x - 1, point.y) in points
            val right = Point(point.x + 1, point.y) in points
            val bottomLeft = Point(point.x - 1, point.y + 1) in points
            val bottom = Point(point.x, point.y + 1) in points
            val bottomRight = Point(point.x + 1, point.y + 1) in points

            if (!left && !top && !right && !bottom) { // no neighbor: 4 corners
                corners += 4
            } else if (left && !right && !top && !bottom) { // one neighbor: 2 corners
                corners += 2
            } else if (!left && top && !right && !bottom) {
                corners += 2
            } else if (!left && !top && right && !bottom) {
                corners += 2
            } else if (!left && !top && !right && bottom) {
                corners += 2
            } else if (left && top && !right && !bottom) { // two neighbors: 0 corner if opposite, 1 outside corner + 1 possible inside corner
                corners += 1
                if (!topLeft) corners += 1
            } else if (left && !top && right && !bottom) {
                corners += 0
            } else if (left && !top && !right && bottom) {
                corners += 1
                if (!bottomLeft) corners += 1
            } else if (!left && top && right && !bottom) {
                corners += 1
                if (!topRight) corners += 1
            } else if (!left && top && !right && bottom) {
                corners += 0
            } else if (!left && !top && right && bottom) {
                corners += 1
                if (!bottomRight) corners += 1
            } else if (left && top && right && !bottom) { // three neighbor; 2 possible inside corner
                if (!topLeft) corners += 1
                if (!topRight) corners += 1
            } else if (left && top && !right && bottom) {
                if (!topLeft) corners += 1
                if (!bottomLeft) corners += 1
            } else if (left && !top && right && bottom) {
                if (!bottomLeft) corners += 1
                if (!bottomRight) corners += 1
            } else if (!left && top && right && bottom) {
                if (!topRight) corners += 1
                if (!bottomRight) corners += 1
            } else if (left && top && right && bottom) { // four neighbors: 4 possible inside corner
                if (!topLeft) corners += 1
                if (!topRight) corners += 1
                if (!bottomLeft) corners += 1
                if (!bottomRight) corners += 1
            }
        }
        return corners
    }
}

data class Garden(private val plants: List<List<Char>>) {
    private val map = mutableMapOf<Char, MutableSet<Point>>()
    private val regions = mutableSetOf<Region>()
    val cost1 get() = regions.sumOf { it.area * it.perimeter }
    val cost2 get() = regions.sumOf { it.area * it.sides }

    init {
        for (x in plants.indices) {
            for (y in plants[x].indices) {
                val point = Point(x, y)
                val plant = plants[x][y]
                map.putIfAbsent(plant, mutableSetOf())
                map[plant]!!.add(point)
            }
        }
        for ((plant, points) in map) {
            val remaining = points.toMutableSet()
            while (remaining.isNotEmpty()) {
                val start = remaining.random()
                val connected = mutableSetOf(start)
                remaining.remove(start)
                val stack = mutableListOf(start)
                while (stack.isNotEmpty()) {
                    val point = stack.removeLast()
                    for (neighbor in point.neighbors.filter { neighbor -> neighbor in remaining }) {
                        connected.add(neighbor)
                        remaining.remove(neighbor)
                        stack.add(neighbor)
                    }
                }
                regions.add(Region(plant, connected))
            }

        }
    }

    companion object {
        fun parse(lines: List<String>) = Garden(lines.map { it.toList() })
    }
}

fun main() {
    val file = File("inputs/d12.txt")
    val lines = file.readLines()

    // part 1
    val garden = Garden.parse(lines)
    println(garden.cost1)

    // part 2
    println(garden.cost2)
}