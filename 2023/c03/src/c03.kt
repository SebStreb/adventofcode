import java.io.File

fun isSymbol(char: Char) = !char.isDigit() && char != '.'

fun check(lines: List<String>, lineIndex: Int, numStartIndex: Int, numStopIndex: Int): Boolean {
    if (lineIndex > 0) {
        val start = maxOf(0, numStartIndex - 1)
        val stop = minOf(numStopIndex + 1, lines[lineIndex - 1].length - 1)
        if ((start..stop).any { colIndex -> isSymbol(lines[lineIndex - 1][colIndex]) }) return true
    }
    if (numStartIndex > 0 && isSymbol(lines[lineIndex][numStartIndex - 1])) return true
    if (numStopIndex < lines[lineIndex].length - 1 && isSymbol(lines[lineIndex][numStopIndex + 1])) return true
    if (lineIndex < lines.size - 1) {
        val start = maxOf(0, numStartIndex - 1)
        val stop = minOf(numStopIndex + 1, lines[lineIndex + 1].length - 1)
        if ((start..stop).any { colIndex -> isSymbol(lines[lineIndex + 1][colIndex]) }) return true
    }
    return false
}

fun part1(lines: List<String>): Int {
    var sum = 0

    for ((lineIndex, line) in lines.withIndex()) {
        var numStartIndex: Int? = null
        var numStopIndex: Int? = null
        for ((colIndex, char) in line.withIndex()) {
            if (char.isDigit()) {
                if (numStartIndex == null) numStartIndex = colIndex
                numStopIndex = colIndex
            } else if (numStartIndex != null && numStopIndex != null) {
                if (check(lines, lineIndex, numStartIndex, numStopIndex)) {
                    sum += line.substring(numStartIndex, numStopIndex + 1).toInt()
                }
                numStartIndex = null
                numStopIndex = null
            }
        }
        if (numStartIndex != null && numStopIndex != null) {
            if (check(lines, lineIndex, numStartIndex, numStopIndex)) {
                sum += line.substring(numStartIndex, numStopIndex + 1).toInt()
            }
        }
    }

    return sum
}

fun expandNumber(line: String, fromIndex: Int): Int {
    var numStartIndex = fromIndex
    var numStopIndex = fromIndex
    while (numStartIndex - 1 >= 0 && line[numStartIndex - 1].isDigit()) numStartIndex -= 1
    while (numStopIndex + 1 <= line.length - 1 && line[numStopIndex + 1].isDigit()) numStopIndex += 1
    return line.substring(numStartIndex, numStopIndex + 1).toInt()
}

fun getNumbersAround(lines: List<String>, lineIndex: Int, colIndex: Int): List<Int> {
    val lst = mutableListOf<Int>()

    if (lineIndex > 0) {
        if (lines[lineIndex - 1][colIndex].isDigit()) {
            lst += expandNumber(lines[lineIndex - 1], colIndex)
        } else {
            if (colIndex > 0 && lines[lineIndex - 1][colIndex - 1].isDigit()) {
                lst += expandNumber(lines[lineIndex - 1], colIndex - 1)
            }
            if (colIndex < lines[lineIndex - 1].length - 1 && lines[lineIndex - 1][colIndex + 1].isDigit()) {
                lst += expandNumber(lines[lineIndex - 1], colIndex + 1)
            }
        }
    }

    if (colIndex > 0 && lines[lineIndex][colIndex - 1].isDigit()) {
        lst += expandNumber(lines[lineIndex], colIndex - 1)
    }

    if (colIndex < lines[lineIndex].length - 1 && lines[lineIndex][colIndex + 1].isDigit()) {
        lst += expandNumber(lines[lineIndex], colIndex + 1)
    }

    if (lineIndex < lines.size - 1) {
        if (lines[lineIndex + 1][colIndex].isDigit()) {
            lst += expandNumber(lines[lineIndex + 1], colIndex)
        } else {
            if (colIndex > 0 && lines[lineIndex + 1][colIndex - 1].isDigit()) {
                lst += expandNumber(lines[lineIndex + 1], colIndex - 1)
            }
            if (colIndex < lines[lineIndex + 1].length - 1 && lines[lineIndex + 1][colIndex + 1].isDigit()) {
                lst += expandNumber(lines[lineIndex + 1], colIndex + 1)
            }
        }
    }

    return lst
}

fun part2(lines: List<String>): Int {
    var sum = 0

    for ((lineIndex, line) in lines.withIndex()) {
        for ((colIndex, char) in line.withIndex()) {
            if (char == '*') {
                val numbers = getNumbersAround(lines, lineIndex, colIndex)
                if (numbers.size == 2) sum += numbers[0] * numbers[1]
            }
        }
    }

    return sum
}

fun main() {
    val file = File("./inputs/c03")
    val lines = file.readLines()
    println(part1(lines))
    println(part2(lines))
}