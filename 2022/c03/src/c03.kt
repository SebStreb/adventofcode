import java.io.File

fun getMisplaced(bag: String): Char {
    val firstHalf = bag.substring(0, bag.length / 2)
    val secondHalf = bag.substring(bag.length / 2)
    return firstHalf.find { c -> secondHalf.contains(c) }!!
}

fun toPriority(c: Char) =
    if (c.isLowerCase()) c - 'a' + 1 else c - 'A' + 27

fun part1() {
    val file = File("./inputs/c03")
    val lines = file.readLines()
    val result = lines.map { getMisplaced(it) }.sumOf { toPriority(it) }
    println(result)
}

data class Group(val bag1: String, val bag2: String, val bag3: String)

fun getGroups(lines: List<String>): List<Group> {
    val group = Group(lines[0], lines[1], lines[2])
    val remaining = lines.drop(3)
    val groups = mutableListOf(group)
    if (remaining.isNotEmpty()) groups.addAll(getGroups(remaining))
    return groups
}

fun getBadge(group: Group): Char = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".first { c ->
    group.bag1.contains(c) && group.bag2.contains(c) && group.bag3.contains(c)
}

fun part2() {
    val file = File("./inputs/c03-2")
    val lines = file.readLines()
    val groups = getGroups(lines)
    val result = groups.map { getBadge(it) }.sumOf { toPriority(it) }
    println(result)
}

fun main() {
    part1()
    part2()
}