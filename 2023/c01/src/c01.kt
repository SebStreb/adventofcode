import java.io.File

fun part1(lines: List<String>): Int {
    var sum = 0

    for (line in lines) {
        val firstDigit = line.first { it.isDigit() }
        val lastDigit = line.last { it.isDigit() }
        val number = (firstDigit + "" + lastDigit).toInt()
        sum += number
    }

    return sum
}

fun part2(lines: List<String>): Int {
    var sum = 0

    for (line in lines) {
        var firstDigit: Char? = null
        for (i in line.indices) {
            if (line[i].isDigit()) firstDigit = line[i]
            if (line.substring(i).startsWith("one")) firstDigit = '1'
            if (line.substring(i).startsWith("two")) firstDigit = '2'
            if (line.substring(i).startsWith("three")) firstDigit = '3'
            if (line.substring(i).startsWith("four")) firstDigit = '4'
            if (line.substring(i).startsWith("five")) firstDigit = '5'
            if (line.substring(i).startsWith("six")) firstDigit = '6'
            if (line.substring(i).startsWith("seven")) firstDigit = '7'
            if (line.substring(i).startsWith("eight")) firstDigit = '8'
            if (line.substring(i).startsWith("nine")) firstDigit = '9'

            if (firstDigit != null) break
        }

        var lastDigit: Char? = null
        for (i in line.indices.reversed()) {
            if (line[i].isDigit()) lastDigit = line[i]
            if (line.substring(0, i+1).endsWith("one")) lastDigit = '1'
            if (line.substring(0, i+1).endsWith("two")) lastDigit = '2'
            if (line.substring(0, i+1).endsWith("three")) lastDigit = '3'
            if (line.substring(0, i+1).endsWith("four")) lastDigit = '4'
            if (line.substring(0, i+1).endsWith("five")) lastDigit = '5'
            if (line.substring(0, i+1).endsWith("six")) lastDigit = '6'
            if (line.substring(0, i+1).endsWith("seven")) lastDigit = '7'
            if (line.substring(0, i+1).endsWith("eight")) lastDigit = '8'
            if (line.substring(0, i+1).endsWith("nine")) lastDigit = '9'

            if (lastDigit != null) break
        }

        val number = (firstDigit!! + "" + lastDigit).toInt()
        sum += number
    }

    return sum
}

fun main() {
    val file = File("./inputs/c01")
    val lines = file.readLines()

    println(part1(lines))
    println(part2(lines))
}