import kotlin.math.max
import kotlin.math.min

class LagoonMap(edges: List<Edge>) {
    private val minX = edges.minOf { min(it.from.x, it.to.x) }
    private val maxX = edges.maxOf { max(it.from.x, it.to.x) }
    private val minY = edges.minOf { min(it.from.y, it.to.y) }
    private val maxY = edges.maxOf { max(it.from.y, it.to.y) }

    private val map = List(maxX - minX + 1) { _ -> MutableList(maxY - minY + 1) { _ -> ' ' } }

    private fun getVertexChar(edgeBefore: Edge, edgeAfter: Edge): Char {
        return if (edgeBefore.direction == Direction.UP && edgeAfter.direction == Direction.RIGHT) 'F'
        else if (edgeBefore.direction == Direction.RIGHT && edgeAfter.direction == Direction.DOWN) '7'
        else if (edgeBefore.direction == Direction.DOWN && edgeAfter.direction == Direction.LEFT) 'J'
        else if (edgeBefore.direction == Direction.LEFT && edgeAfter.direction == Direction.UP) 'L'
        else if (edgeBefore.direction == Direction.UP && edgeAfter.direction == Direction.LEFT) '7'
        else if (edgeBefore.direction == Direction.LEFT && edgeAfter.direction == Direction.DOWN) 'F'
        else if (edgeBefore.direction == Direction.DOWN && edgeAfter.direction == Direction.RIGHT) 'L'
        else if (edgeBefore.direction == Direction.RIGHT && edgeAfter.direction == Direction.UP) 'J'
        else error("Should not happen")
    }

    init {
        for (edge in edges) {
            var x = edge.from.x - minX
            var y = edge.from.y - minY
            repeat(edge.length) {
                map[x][y] = if (edge.direction in listOf(Direction.UP, Direction.DOWN)) '|' else '-'
                when (edge.direction) {
                    Direction.UP -> x--
                    Direction.DOWN -> x++
                    Direction.LEFT -> y--
                    Direction.RIGHT -> y++
                }
            }
        }

        for (i in 1..<edges.size) {
            val x = edges[i].from.x - minX
            val y = edges[i].from.y - minY
            map[x][y] = getVertexChar(edges[i - 1], edges[i])
        }

        val x = edges.first().from.x - minX
        val y = edges.first().from.y - minY
        map[x][y] = getVertexChar(edges.last(), edges.first())
    }

    fun countInside(): Int {
        var count = 0
        for (i in map.indices) {
            var inside = false
            for (j in map[i].indices) {
                if (map[i][j] in listOf('|', '7', 'F')) inside = !inside

                if (map[i][j] != ' ') count++
                else if (inside) count++
            }
        }
        return count
    }

    override fun toString() = map.joinToString("\n") { list ->
        list.joinToString("") { cell -> cell.toString() }
    }
}