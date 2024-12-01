import java.io.File
import kotlin.math.abs

fun getLines(fileName: String): List<String> {
    val inputFile = File(fileName)
    return inputFile.readLines()
}

fun getNumbers(lines: List<String>): Pair<List<Int>, List<Int>> {
    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()

    for (line in lines) {
        val numbers = line.split("   ")
        list1.add(numbers[0].toInt())
        list2.add(numbers[1].toInt())
    }

    return Pair(list1, list2)
}

fun part1(list1: List<Int>, list2: List<Int>) {
    var sum = 0
    for (i in list1.indices) sum += abs(list1[i] - list2[i])
    println(sum)
}

fun part2(list1: List<Int>, list2: List<Int>) {
    var sum = 0
    for (a in list1) {
        val b = list2.count { it == a }
        sum += a * b
    }
    println(sum)
}

fun main() {
    val lines = getLines("inputs/d01.txt")

    val (list1, list2) = getNumbers(lines)

    part1(list1.sorted(), list2.sorted())
    part2(list1, list2)
}