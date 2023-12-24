import java.io.File

data class Tile(val x: Int, val y: Int, val dist: Int) {
    fun findNext(map: List<List<Char>>): Tile? {
        val tile = when (map[x][y]) {
            '|' -> {
                if (!isInLoop(x - 1, y)) Tile(x - 1, y, dist + 1)
                else if (!isInLoop(x + 1, y)) Tile(x + 1, y, dist + 1)
                else null
            }

            '-' -> {
                if (!isInLoop(x, y - 1)) Tile(x, y - 1, dist + 1)
                else if (!isInLoop(x, y + 1)) Tile(x, y + 1, dist + 1)
                else null
            }

            'L' -> {
                if (!isInLoop(x - 1, y)) Tile(x - 1, y, dist + 1)
                else if (!isInLoop(x, y + 1)) Tile(x, y + 1, dist + 1)
                else null
            }

            'J' -> {
                if (!isInLoop(x - 1, y)) Tile(x - 1, y, dist + 1)
                else if (!isInLoop(x, y - 1)) Tile(x, y - 1, dist + 1)
                else null
            }

            '7' -> {
                if (!isInLoop(x + 1, y)) Tile(x + 1, y, dist + 1)
                else if (!isInLoop(x, y - 1)) Tile(x, y - 1, dist + 1)
                else null
            }

            'F' -> {
                if (!isInLoop(x + 1, y)) Tile(x + 1, y, dist + 1)
                else if (!isInLoop(x, y + 1)) Tile(x, y + 1, dist + 1)
                else null
            }

            else -> error("Should not happen")
        }
        if (tile == null) return null

        found += tile
        return tile
    }

    companion object {
        val found = mutableSetOf<Tile>()

        private fun getStartNeighbors(map: List<List<Char>>, x: Int, y: Int): Pair<Tile?, Tile?> {
            val neighbors = mutableListOf<Tile>()

            if (x > 0 && map[x - 1][y] in listOf('|', '7', 'F')) neighbors += Tile(x - 1, y, 1)
            if (y < map[x].size - 1 && map[x][y + 1] in listOf('-', 'J', '7')) neighbors += Tile(x, y + 1, 1)
            if (x < map.size - 1 && map[x + 1][y] in listOf('|', 'L', 'J')) neighbors += Tile(x + 1, y, 1)
            if (y > 0 && map[x][y - 1] in listOf('-', 'L', 'F')) neighbors += Tile(x, y - 1, 1)

            found += neighbors.first()
            found += neighbors.last()
            return Pair(neighbors.first(), neighbors.last())
        }

        fun expandFrom(start: Tile, map: List<List<Char>>): Tile {
            var (current1, current2) = getStartNeighbors(map, start.x, start.y)
            while (current1 != null && current2 != null) {
                current1 = current1.findNext(map)
                current2 = current2.findNext(map)
            }

            if (current1 == null && current2 == null) error("Need to think of something")
            return current1 ?: current2!!
        }

        private fun isInLoop(x: Int, y: Int) = found.any { it.x == x && it.y == y }

        private fun getStartType(map: List<List<Char>>, x: Int, y: Int): Char {
            val neighbors = mutableSetOf<String>()

            if (x > 0 && map[x - 1][y] in listOf('|', '7', 'F')) neighbors += "north"
            if (y < map[x].size - 1 && map[x][y + 1] in listOf('-', 'J', '7')) neighbors += "east"
            if (x < map.size - 1 && map[x + 1][y] in listOf('|', 'L', 'J')) neighbors += "south"
            if (y > 0 && map[x][y - 1] in listOf('-', 'L', 'F')) neighbors += "west"

            return when (neighbors) {
                setOf("north", "south") -> '|'
                setOf("east", "west") -> '-'
                setOf("north", "east") -> 'L'
                setOf("north", "west") -> 'J'
                setOf("south", "west") -> '7'
                setOf("south", "east") -> 'F'
                else -> error("Should not happen")
            }
        }

        fun replaceStart(map: List<List<Char>>) = List(map.size) { i ->
            List(map[i].size) { j ->
                if (map[i][j] != 'S') map[i][j] else getStartType(map, i, j)
            }
        }

        fun countInside(map: List<List<Char>>): Int {
            var count = 0
            for (i in map.indices) {
                var inside = false
                for (j in map[i].indices) {
                    if (!isInLoop(i, j)) {
                        if (inside) count++
                    } else {
                        if (map[i][j] in listOf('|', '7', 'F')) inside = !inside
                    }
                }
            }
            return count
        }
    }
}

fun main() {
    val file = File("./inputs/c10")
    val lines = file.readLines()

    // parsing
    val map = lines.map { it.toCharArray().toList() }
    val startX = map.indexOfFirst { row -> row.any { it == 'S' } }
    val startY = map[startX].indexOfFirst { it == 'S' }
    val start = Tile(startX, startY, 0)
    Tile.found += start

    // part 1
    val last = Tile.expandFrom(start, map)
    println(last.dist)

    // part 2
    val map2 = Tile.replaceStart(map)
    println(Tile.countInside(map2))
}