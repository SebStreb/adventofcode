import java.io.File

open class WordSearch(val letters: List<List<Char>>) {
    private val size = letters.size
    private val target = "XMAS"
    private val limit = size - target.length

    private fun countHorizontal(): Int {
        var count = 0
        for (row in letters) {
            for (i in 0..limit) {
                val word = row.slice(i until i + target.length).joinToString("")
                if (word == target) count++
            }
        }
        return count
    }

    private fun flipHorizontal(): Int {
        val flipped = letters.map { it.reversed() }
        return WordSearch(flipped).countHorizontal()
    }

    private fun countVertical(): Int {
        var count = 0
        for (i in 0..limit) {
            for (j in 0 until size) {
                val word = target.indices.map { letters[i + it][j] }.joinToString("")
                if (word == target) count++
            }
        }
        return count
    }

    private fun flipVertical(): Int {
        val flipped = letters.reversed()
        return WordSearch(flipped).countVertical()
    }

    private fun countDiagonal(): Int {
        var count = 0
        for (i in 0..limit) {
            for (j in 0..limit) {
                val word = target.indices.map { letters[i + it][j + it] }.joinToString("")
                if (word == target) count++
            }
        }
        return count
    }

    private fun flipDiagonal(): Int {
        val flipped = letters.map { it.reversed() }.reversed()
        return WordSearch(flipped).countDiagonal()
    }

    private fun countAntiDiagonal(): Int {
        var count = 0
        for (i in 0..limit) {
            for (j in target.length - 1 until size) {
                val word = target.indices.map { letters[i + it][j - it] }.joinToString("")
                if (word == target) count++
            }
        }
        return count
    }

    private fun flipAntiDiagonal(): Int {
        val flipped = letters.map { it.reversed() }.reversed()
        return WordSearch(flipped).countAntiDiagonal()
    }

    open fun count(): Int {
        return countHorizontal() + flipHorizontal() +
               countVertical() + flipVertical() +
               countDiagonal() + flipDiagonal() +
               countAntiDiagonal() + flipAntiDiagonal()
    }

    fun toWordSearch2() = WordSearch2(letters)

    companion object {
        fun parse (lines: List<String>) = WordSearch(lines.map { it.toList() })
    }
}

class WordSearch2(letters: List<List<Char>>) : WordSearch(letters) {
    private val size = letters.size
    private val target = "MAS"

    private fun checkDiagonal(i: Int, j: Int): Boolean {
        val word1 = letters[i-1][j-1].toString() + letters[i][j] + letters[i+1][j+1]
        val word2 = letters[i+1][j+1].toString() + letters[i][j] + letters[i-1][j-1]
        return word1 == target || word2 == target
    }

    private fun checkAntiDiagonal(i: Int, j: Int): Boolean {
        val word1 = letters[i-1][j+1].toString() + letters[i][j] + letters[i+1][j-1]
        val word2 = letters[i+1][j-1].toString() + letters[i][j] + letters[i-1][j+1]
        return word1 == target || word2 == target
    }

    override fun count(): Int {
        var count = 0
        for (i in 1 until (size-1)) {
            for (j in 1 until (size-1)) {
                if (letters[i][j] == 'A' && checkDiagonal(i, j) && checkAntiDiagonal(i, j)) count++
            }
        }
        return count
    }

}

fun main() {
    val file = File("inputs/d04.txt")
    val lines = file.readLines()

    // part 1
    val wordSearch = WordSearch.parse(lines)
    println(wordSearch.count())

    // part 2
    val wordSearch2 = wordSearch.toWordSearch2()
    println(wordSearch2.count())
}