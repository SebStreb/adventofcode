import java.io.File

class Pattern(
    private val grid: List<List<Char>>,
    private val bannedRowSplit: Int? = null,
    private val bannedColSplit: Int? = null,
) {

    private val rowSplit = findRowSplit()
    private val colSplit = findColSplit()
    val summarize = if (rowSplit != null) rowSplit * 100 else colSplit ?: -1

    private fun getRow(i: Int) = grid[i].joinToString("")
    private fun getCol(j: Int) = grid.indices.map { i -> grid[i][j] }.joinToString("")

    private fun isRowSplit(i: Int): Boolean {
        var up = i - 1
        var down = i
        while (up >= 0 && down < grid.size) {
            if (getRow(up) != getRow(down)) return false
            up--
            down++
        }
        return true
    }

    private fun findRowSplit(): Int? {
        var previous = getRow(0)
        for (i in 1..<grid.size) {
            val current = getRow(i)
            if (i != bannedRowSplit && previous == current && isRowSplit(i)) return i
            previous = current
        }
        return null
    }

    private fun isColSplit(j: Int): Boolean {
        var left = j - 1
        var right = j
        while (left >= 0 && right < grid[0].size) {
            if (getCol(left) != getCol(right)) return false
            left--
            right++
        }
        return true
    }

    private fun findColSplit(): Int? {
        var previous = getCol(0)
        for (j in 1..<grid[0].size) {
            val current = getCol(j)
            if (j != bannedColSplit && previous == current && isColSplit(j)) return j
            previous = current
        }
        return null
    }

    private fun invert(x: Int, y: Int) = Pattern(
        List(size = grid.size) { i ->
            List(grid[i].size) { j ->
                if (i == x && j == y) {
                    if (grid[i][j] == '.') '#' else '.'
                } else grid[i][j]
            }
        },
        rowSplit, colSplit,
    )

    fun getAlternate(): Int {
        for (i in grid.indices) {
            for (j in grid[i].indices) {
                val pattern = invert(i, j)
                if (pattern.summarize > 0 && pattern.summarize != summarize) return pattern.summarize
            }
        }
        error("Not found")
    }

    override fun toString() = grid.joinToString("\n") { it.joinToString("") }

    companion object {
        fun parse(lines: List<String>): List<Pattern> {
            val patterns = mutableListOf<Pattern>()
            var grid = mutableListOf<List<Char>>()
            for (line in lines) {
                if (line.isEmpty()) {
                    patterns += Pattern(grid)
                    grid = mutableListOf()
                } else grid += line.toCharArray().toList()
            }
            if (grid.isNotEmpty()) patterns += Pattern(grid)
            return patterns
        }
    }

}

fun main() {
    val file = File("./inputs/c13")
    val lines = file.readLines()

    // part 1
    val patterns = Pattern.parse(lines)
    println(patterns.sumOf { it.summarize })

    // part 2
    println(patterns.sumOf { it.getAlternate() })
}