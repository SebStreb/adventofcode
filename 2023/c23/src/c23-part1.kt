import java.io.File

data class Pos(val x: Int, val y: Int)

data class Path(val positions: List<Pos>) {
    val length get() = positions.size - 1

    fun isOver(map: List<List<Char>>) = positions.last().x == map.size - 1

    fun next(map: List<List<Char>>): List<Path> {
        val lastPos = positions.last()

        if (map[lastPos.x][lastPos.y] == '^') return listOf(Path(positions + Pos(lastPos.x - 1, lastPos.y)))
        if (map[lastPos.x][lastPos.y] == '>') return listOf(Path(positions + Pos(lastPos.x, lastPos.y + 1)))
        if (map[lastPos.x][lastPos.y] == 'v') return listOf(Path(positions + Pos(lastPos.x + 1, lastPos.y)))
        if (map[lastPos.x][lastPos.y] == '<') return listOf(Path(positions + Pos(lastPos.x, lastPos.y - 1)))

        val res = mutableListOf<Path>()

        if (lastPos.x > 0 && Pos(lastPos.x - 1, lastPos.y) !in positions && (map[lastPos.x - 1][lastPos.y] in listOf('.', '^'))) {
            res.add(Path(positions + Pos(lastPos.x - 1, lastPos.y)))
        }

        if (lastPos.y < map[0].size && Pos(lastPos.x, lastPos.y + 1) !in positions && (map[lastPos.x][lastPos.y + 1] in listOf('.', '>'))) {
            res.add(Path(positions + Pos(lastPos.x, lastPos.y + 1)))
        }

        if (lastPos.x < map.size && Pos(lastPos.x + 1, lastPos.y) !in positions && (map[lastPos.x + 1][lastPos.y] in listOf('.', 'v'))) {
            res.add(Path(positions + Pos(lastPos.x + 1, lastPos.y)))
        }

        if (lastPos.y > 0 && Pos(lastPos.x, lastPos.y - 1) !in positions && (map[lastPos.x][lastPos.y - 1] in listOf('.', '<'))) {
            res.add(Path(positions + Pos(lastPos.x, lastPos.y - 1)))
        }

        return res
    }
}

class Hike(private val map: List<List<Char>>) {
    private val startPath = Path(listOf(Pos(0, map[0].indexOf('.'))))

    private val openPaths = mutableListOf(startPath)
    val closedPaths = mutableListOf<Path>()

    fun walk() {
        while (openPaths.isNotEmpty()) {
            val path = openPaths.removeFirst()
            if (path.isOver(map)) closedPaths.add(path)
            else openPaths.addAll(path.next(map))
        }
    }

    companion object {
        fun parse(lines: List<String>) = Hike(lines.map { it.toList() })
    }
}

fun main() {
    val file = File("inputs/c23")
    val lines = file.readLines()

    // part 1
    val hike = Hike.parse(lines)
    hike.walk()
    println(hike.closedPaths.maxOf { it.length })
}