import java.io.File

data class Tree(val height: Int) : Comparable<Tree> {
    override fun compareTo(other: Tree) = this.height - other.height
}

class Forest(val trees: List<List<Tree>>) {
    val size: Int get() = trees.size
    val nbVisibleTrees: Int
        get() {
            var count = 0
            for (row in trees.indices) for (col in trees[row].indices) if (visible(row, col)) count++
            return count
        }

    fun visible(x: Int, y: Int): Boolean {
        if ((x == 0 || x == size - 1) && (y == 0 || y == size - 1)) return true

        if ((0..<x).all { i -> trees[i][y] < trees[x][y] }) return true

        if (((x+1)..<size).all { i -> trees[i][y] < trees[x][y] }) return true

        if ((0..<y).all { j -> trees[x][j] < trees[x][y] }) return true

        if (((y+1)..<size).all { j -> trees[x][j] < trees[x][y] }) return true

        return false
    }

    fun scenicScore(x: Int, y: Int): Int {

        var left = 1

        var i: Int = x-1
        while (i > 0 && trees[i][y] < trees[x][y]) {
            left++
            i--
        }

        var right = 1

        i = x+1
        while (i < size-1 && trees[i][y] < trees[x][y]) {
            right++
            i++
        }

        var up = 1

        var j: Int = y-1
        while (j > 0 && trees[x][j] < trees[x][y]) {
            up++
            j--
        }

        var down = 1

        j = y+1
        while (j < size-1 && trees[x][j] < trees[x][y]) {
            down++
            j++
        }

        return left * down * up * right
    }

    companion object {
        fun create(lines: List<String>): Forest {
            val trees = mutableListOf<List<Tree>>()
            lines.filter { it.isNotEmpty() }.forEach { line ->
                trees.add(line.split("").filter { it.isNotEmpty() }.map { Tree(it.toInt()) })
            }
            return Forest(trees)
        }
    }
}

fun main() {
    val file = File("./inputs/c08")
    val lines = file.readLines()
    val forest = Forest.create(lines)

    var bestScore = 0
    for (row in forest.trees.indices) {
        for (col in forest.trees.indices) {
            val score = forest.scenicScore(row, col)
            if (bestScore < score) {
                bestScore = score
            }
        }
    }

    println(bestScore)
}