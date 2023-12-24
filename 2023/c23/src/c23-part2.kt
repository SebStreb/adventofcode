import java.io.File

class Map(private val map: List<List<Char>>) {
    val startPosition = Position(0, map[0].indexOf('.'))
    val endPosition = Position(map.size - 1, map[map.size - 1].indexOf('.'))

    fun get(position: Position) = map[position.x][position.y]
    fun isValid(position: Position) = position.x in map.indices && position.y in map[0].indices

    companion object {
        fun parse(lines: List<String>) = Map(lines.map { it.toList().map { c -> if (c != '#') '.' else c } })
    }
}

data class Position(val x: Int, val y: Int) : Comparable<Position> {
    fun next(map: Map, from: Position? = null) = listOf(
        Position(x - 1, y),
        Position(x + 1, y),
        Position(x, y - 1),
        Position(x, y + 1),
    ).filter { it != from && map.isValid(it) && map.get(it) == '.' }

    fun isIntersection(map: Map) = this == map.startPosition || this == map.endPosition || next(map).size > 2

    override fun compareTo(other: Position) = if (x == other.x) y.compareTo(other.y) else x.compareTo(other.x)
}

data class Edge(val from: Position, val to: Position, val dist: Int)

class Graph(private val map: Map) {
    val intersections = mutableListOf(map.startPosition)
    private val edges = mutableSetOf<Edge>()

    init {
        val next = map.startPosition.next(map)
        findNextIntersection(next.first(), map.startPosition)
    }

    private fun findNextIntersection(from: Position, previousIntersection: Position) {
        var steps = 1
        var previous = previousIntersection
        var current = from
        var next = current.next(map, previous)
        while (next.size == 1) {
            steps++
            previous = current
            current = next.first()
            next = current.next(map, previous)
        }

        if (current.isIntersection(map)) {
            if (previousIntersection < current) edges.add(Edge(previousIntersection, current, steps))
            else edges.add(Edge(current, previousIntersection, steps))

            if (current !in intersections) {
                intersections.add(current)
                current.next(map, previous).forEach { findNextIntersection(it, current) }
            }
        }
    }

    fun getAdjacencyMatrix() = intersections.map { a ->
        intersections.map { b ->
            val from = if (a < b) a else b
            val to = if (a < b) b else a
            val edge = edges.firstOrNull { it.from == from && it.to == to }
            edge?.dist ?: 0
        }
    }
}

class Parkour(private val intersections: List<Position>, private val matrix: List<List<Int>>) {
    fun maxDistance(from: Position, to: Position) =
        maxDistance(intersections.indexOf(from), intersections.indexOf(to), emptyList())

    private fun maxDistance(from: Int, to: Int, visited: List<Int>): Int {
        if (from == to) return 0
        val distances = mutableListOf<Int>()
        for ((other, dist) in matrix[from].withIndex()) {
            if (dist == 0) continue
            if (other in visited) continue
            distances += dist + maxDistance(other, to, visited + from)
        }
        return distances.maxOrNull() ?: Int.MIN_VALUE
    }
}

fun main() {
    val file = File("inputs/c23")
    val map = Map.parse(file.readLines())

    val graph = Graph(map)
    val parkour = Parkour(graph.intersections, graph.getAdjacencyMatrix())
    println(parkour.maxDistance(map.startPosition, map.endPosition))
}