import java.io.File

class Onsen(private var towels: Set<String>, private val patterns: List<String>) {
    // used for part 1 without memoization
    private val optimizedTowels = optimize()

    private fun optimize(): Set<String> {
        val colors = towels.flatMap { it.toSet() }.toSet()
        val singleColorTowels = towels.filter { it.length == 1 }.toSet()
        val singleColors = singleColorTowels.map { it.first() }.toSet()
        val missingColors = colors - singleColors
        val missingColorTowels = towels.filter { towel -> missingColors.any { color -> towel.contains(color) } }.toSet()
        val possibleTowels = singleColorTowels + missingColorTowels
        val redundant = possibleTowels.mapNotNull { towel ->
            val smaller = possibleTowels.filter { it.length < towel.length }
            for (towel1 in smaller) for (towel2 in smaller) if (towel1 + towel2 == towel) return@mapNotNull towel
            return@mapNotNull null
        }.toSet()
        return possibleTowels - redundant
    }

    private fun isValid(pattern: String): Boolean {
        if (pattern.isEmpty()) return true
        for (towel in optimizedTowels) if (pattern.startsWith(towel) && isValid(pattern.drop(towel.length))) return true
        return false
    }

    private val cache = mutableMapOf<String, Long>()
    private fun countTowelArrangements(pattern: String): Long {
        if (pattern in cache) return cache[pattern]!!
        if (pattern.isEmpty()) return 1L
        return towels
            .filter { pattern.startsWith(it) }
            .sumOf { countTowelArrangements(pattern.drop(it.length)) }
            .also { cache[pattern] = it }
    }

    // part 1 without memoization :
    // fun part1() = patterns.count { isValid(it) }

    fun part1() = patterns.count { countTowelArrangements(it) != 0L }

    fun part2() = patterns.sumOf { countTowelArrangements(it) }

    constructor(lines: List<String>) : this(lines.first().split(", ").toSet(), lines.drop(2))
}

fun main() {
    val test = false
    val file = File(if (test) "inputs/test.txt" else "inputs/d19.txt")
    val lines = file.readLines()
    val onsen = Onsen(lines)

    // part 1
    println(onsen.part1())

    // part 2
    println(onsen.part2())
}