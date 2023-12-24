import java.io.File

class Platform(private val grid: MutableList<MutableList<Char>>) {

    private fun slideNorth(x: Int, y: Int) {
        var i = x
        while (i > 0 && grid[i - 1][y] == '.') {
            grid[i - 1][y] = grid[i][y]
            grid[i][y] = '.'
            i--
        }
    }

    private fun slideEast(x: Int, y: Int) {
        var j = y
        while (j < grid[x].size - 1 && grid[x][j + 1] == '.') {
            grid[x][j + 1] = grid[x][j]
            grid[x][j] = '.'
            j++
        }
    }

    private fun slideSouth(x: Int, y: Int) {
        var i = x
        while (i < grid.size - 1 && grid[i + 1][y] == '.') {
            grid[i + 1][y] = grid[i][y]
            grid[i][y] = '.'
            i++
        }
    }

    private fun slideWest(x: Int, y: Int) {
        var j = y
        while (j > 0 && grid[x][j - 1] == '.') {
            grid[x][j - 1] = grid[x][j]
            grid[x][j] = '.'
            j--
        }
    }

    fun tiltNorth() {
        for (i in grid.indices) for (j in grid[i].indices) if (grid[i][j] == 'O') slideNorth(i, j)
    }

    private fun tiltEast() {
        for (i in grid.indices) for (j in grid[i].indices.reversed()) if (grid[i][j] == 'O') slideEast(i, j)
    }

    private fun tiltSouth() {
        for (i in grid.indices.reversed()) for (j in grid[i].indices) if (grid[i][j] == 'O') slideSouth(i, j)
    }

    private fun tiltWest() {
        for (i in grid.indices) for (j in grid[i].indices) if (grid[i][j] == 'O') slideWest(i, j)
    }

    fun tilt() {
        tiltNorth()
        tiltWest()
        tiltSouth()
        tiltEast()
    }

    private val cache = mutableMapOf<String, Int>()
    fun tiltCycle(cycle: Int): Int {
        tilt()

        val state = toString()
        if (state in cache) return cache[state]!!
        cache[state] = cycle
        return -1
    }

    fun getNorthLoad(): Int {
        var load = 0
        var contribution = 1
        for (i in grid.indices.reversed()) {
            for (j in grid[i].indices) {
                if (grid[i][j] == 'O') load += contribution
            }
            contribution++
        }
        return load
    }

    override fun toString() = grid.joinToString("\n") { it.joinToString("") }

    companion object {
        fun parse(lines: List<String>) = Platform(lines.map { it.toCharArray().toMutableList() }.toMutableList())
    }

}

fun main() {
    val file = File("./inputs/c14")
    val lines = file.readLines()

    // part 1
    var platform = Platform.parse(lines)
    platform.tiltNorth()
    println(platform.getNorthLoad())

    // part 2
    platform = Platform.parse(lines)

    var cycleEnd = 0
    var cycleStart = -1
    while (cycleStart == -1) {
        cycleEnd++
        cycleStart = platform.tiltCycle(cycleEnd)
    }

    val cycleLength = cycleEnd - cycleStart
    var finalCycle = cycleStart
    while (finalCycle + cycleLength < 1000000000) finalCycle += cycleLength

    while (finalCycle < 1000000000) {
        finalCycle++
        platform.tilt()
    }
    println(platform.getNorthLoad())
}