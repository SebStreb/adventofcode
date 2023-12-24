import java.io.File
import kotlin.math.abs

data class Galaxy(val x: Int, val y: Int) {
    operator fun compareTo(that: Galaxy) = if (this.x == that.x) this.y - that.y else this.x - that.x

    companion object {
        fun getDistance(g1: Galaxy, g2: Galaxy) = (abs(g1.x - g2.x) + abs(g1.y - g2.y)).toLong()
    }
}

class Universe(map: List<List<Char>>, private val expansionFactor: Int) {
    private val galaxies = mutableListOf<Galaxy>()
    private val emptyRows = mutableListOf<Int>()
    private val emptyCols = mutableListOf<Int>()

    init {
        for (i in map.indices) for (j in map[i].indices) if (map[i][j] == '#') galaxies += Galaxy(i, j)
        for (i in map.indices) if (map[i].indices.all { j -> map[i][j] == '.' }) emptyRows += i
        for (j in map[0].indices) if (map.indices.all { i -> map[i][j] == '.' }) emptyCols += j
    }

    fun expand() {
        for (i in galaxies.indices) {
            val countEmptyRows = emptyRows.count { it < galaxies[i].x }
            val countEmptyCols = emptyCols.count { it < galaxies[i].y }
            val dX = (countEmptyRows * expansionFactor) - countEmptyRows
            val dY = (countEmptyCols * expansionFactor) - countEmptyCols
            galaxies[i] = Galaxy(galaxies[i].x + dX, galaxies[i].y + dY)
        }
    }

    fun getGalaxyPairs(): List<Pair<Galaxy, Galaxy>> {
        val pairs = mutableListOf<Pair<Galaxy, Galaxy>>()
        for (g1 in galaxies) for (g2 in galaxies) if (g1 < g2) pairs += Pair(g1, g2)
        return pairs
    }

    companion object {
        fun parse(lines: List<String>, expansionFactor: Int) = Universe(lines.map { it.toList() }, expansionFactor)
    }
}

fun main() {
    val file = File("./inputs/c11")
    val lines = file.readLines()

    // part 1
    val universe = Universe.parse(lines, 2)
    universe.expand()
    println(universe.getGalaxyPairs().sumOf { Galaxy.getDistance(it.first, it.second) })

    // part 2
    val universe2 = Universe.parse(lines, 1000000)
    universe2.expand()
    println(universe2.getGalaxyPairs().sumOf { Galaxy.getDistance(it.first, it.second) })
}