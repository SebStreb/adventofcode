import java.io.File

data class Key(val heights: List<Int>)
data class Lock(val heights: List<Int>)

class Office(private val keys: List<Key>, private val locks: List<Lock>) {
    private fun match(key: Key, lock: Lock) = key.heights.indices.all { key.heights[it] + lock.heights[it] < 6 }

    fun countMatches() = keys.sumOf { key -> locks.count { lock -> match(key, lock) } }

    companion object {
        private fun getHeights(lines: List<String>): List<Int> {
            val heights = mutableListOf(0, 0, 0, 0, 0)
            for (line in lines.drop(1).dropLast(1)) for (i in line.indices) if (line[i] == '#') heights[i]++
            return heights
        }

        private fun parseKey(lines: List<String>) = Key(getHeights(lines))
        private fun parseLock(lines: List<String>) = Lock(getHeights(lines))

        fun parse(lines: List<String>): Office {
            val keys = mutableListOf<Key>()
            val locks = mutableListOf<Lock>()

            var index = 0
            while (index < lines.size) {
                when (lines[index]) {
                    "....." -> keys += parseKey(lines.subList(index, index + 7))
                    "#####" -> locks += parseLock(lines.subList(index, index + 7))
                    else -> error("Invalid line: ${lines[index]}")
                }
                index += 8
            }

            return Office(keys, locks)
        }
    }
}

fun main() {
    val test = false
    val file = File(if (test) "inputs/test.txt" else "inputs/d25.txt")
    val lines = file.readLines()

    val office = Office.parse(lines)
    println(office.countMatches())
}