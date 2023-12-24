import java.io.File

class Springs(conditions: String, groups: List<Int>) {
    private val cache = mutableMapOf<Triple<String, List<Int>, Int>, Long>()
    val perms = countPermutation(conditions, groups, 0)

    private fun countPermutation(conditions: String, groups: List<Int>, currGroup: Int): Long {
        if (cache.containsKey(Triple(conditions, groups, currGroup))) return cache[Triple(conditions, groups, currGroup)]!!

        val count = if (conditions.isEmpty()) {
            if (currGroup != 0) {
                if (groups.size != 1) 0 else if (groups.first() != currGroup) 0 else 1
            } else if (groups.isEmpty()) 1 else 0
        } else when (conditions.first()) {
            '.' -> if (currGroup != 0) {
                if (groups.isEmpty()) 0
                else if (groups.first() != currGroup) 0
                else countPermutation(conditions.drop(1), groups.drop(1), 0)
            } else countPermutation(conditions.drop(1), groups, 0)

            '#' -> if (groups.isEmpty()) 0
            else if (currGroup + 1 > groups.first()) 0
            else countPermutation(conditions.drop(1), groups, currGroup + 1)

            '?' -> countPermutation(
                ".${conditions.drop(1)}",
                groups,
                currGroup,
            ) + countPermutation(
                "#${conditions.drop(1)}",
                groups,
                currGroup,
            )

            else -> error("Should not happen")
        }
        cache[Triple(conditions, groups, currGroup)] = count
        return count
    }

    companion object {
        private fun parseOne(line: String): Springs {
            val data = line.split(' ')
            val groups = data[1].split(',').map { it.toInt() }
            return Springs(data[0], groups)
        }

        fun parse(lines: List<String>) = lines.map { parseOne(it) }

        fun parseOne2(line: String): Springs {
            val data = line.split(' ')
            val springs = listOf(data[0], data[0], data[0], data[0], data[0]).joinToString("?")
            val groups = listOf(data[1], data[1], data[1], data[1], data[1]).joinToString(",")
            return parseOne(listOf(springs, groups).joinToString(" "))
        }
    }
}

fun main() {
    val file = File("./inputs/c12")
    val lines = file.readLines()

    // part 1
    val springs = Springs.parse(lines)
    println(springs.sumOf { it.perms })

    // part 2
    println(lines.sumOf { line -> Springs.parseOne2(line).perms })
}