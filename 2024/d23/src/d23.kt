import java.io.File

fun parse(lines: List<String>): Pair<Set<String>, Set<Pair<String, String>>> {
    val computers = mutableSetOf<String>()
    val links = mutableSetOf<Pair<String, String>>()

    for (line in lines) {
        val (c1, c2) = line.split('-')
        computers += c1
        computers += c2
        links += c1 to c2
    }

    return computers to links
}

fun part1(lines: List<String>): Int {
    val (computers, links) = parse(lines)

    val threeConnected = mutableSetOf<Set<String>>()
    for ((c1, c2) in links) {
        for (computer in computers) {
            val connection1 = c1 to computer in links || computer to c1 in links
            val connection2 = c2 to computer in links || computer to c2 in links
            if (connection1 && connection2) threeConnected += setOf(c1, c2, computer)
        }
    }

    return threeConnected.count { set -> set.any { it.startsWith("t") } }
}


fun part2(lines: List<String>): String {
    val (computers, links) = parse(lines)

    var groups = links.map { setOf(it.first, it.second) }.toSet()

    while (groups.size > 1) {
        val next = mutableSetOf<Set<String>>()
        for (group in groups) {
            val possibilities = mutableSetOf<String>()
            for (computer in computers) {
                if (group.all { c -> computer to c in links || c to computer in links }) possibilities += computer
            }
            for (possibility in possibilities) next += group + possibility
        }
        groups = next
    }

    return groups.first().sorted().joinToString(",")
}

fun main() {
    val test = false
    val file = File(if (test) "inputs/test.txt" else "inputs/d23.txt")
    val lines = file.readLines()

    // part 1
    println(part1(lines))

    // part 2
    println(part2(lines))
}