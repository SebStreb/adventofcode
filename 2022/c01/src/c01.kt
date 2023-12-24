import java.io.File

fun read(lines: List<String>): MutableMap<Int, List<Int>> {
    val elves = mutableMapOf<Int, List<Int>>()

    var elfIndex = 1
    var elfValues = mutableListOf<Int>()
    for (line in lines) {
        if (line.isBlank()) {
            elves[elfIndex] = elfValues
            elfIndex++
            elfValues = mutableListOf()
        } else {
            elfValues += line.toInt()
        }
    }

    return elves
}

fun findBest(elves: Map<Int, List<Int>>): Int {
    var bestElfValue = 0

    elves.forEach { (_, values) ->
        val value = values.sum()
        if (bestElfValue < value) {
            bestElfValue = value
        }
    }

    return bestElfValue
}

fun findBestThree(elves: Map<Int, List<Int>>): Int =
    elves.map { (_, values) -> values.sum() }.sorted().reversed().take(3).sum()

fun main() {
    val file = File("./inputs/c01")
    val lines = file.readLines()
    val elves = read(lines)

    // part 1
    println(findBest(elves))

    // part 2
    val result = findBestThree(elves)
    println(result)
}