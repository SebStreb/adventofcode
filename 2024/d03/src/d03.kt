import java.io.File

data class Mul(val x: Int, val y: Int) {
    val result get() = x * y

    companion object {
        fun result(muls: List<Mul>) = muls.sumOf { it.result }

        fun parse(text: String): List<Mul> {
            val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
            val results = regex.findAll(text)
            return results.map { result ->
                val (xStr, yStr) = result.destructured
                Mul(xStr.toInt(), yStr.toInt())
            }.toList()
        }
    }
}

fun program(text: String): Int {
    var sum = 0

    val regex = Regex("""(mul\((\d{1,3}),(\d{1,3})\)|do\(\)|don't\(\))""")

    var index = 0
    var enabled = true
    while (index < text.length) {
        val match = regex.find(text, index) ?: break

        if (match.groupValues[0].startsWith("mul")) {
            if (enabled) {
                val x = match.groupValues[2].toInt()
                val y = match.groupValues[3].toInt()
                sum += x * y
            }
        } else if (match.groupValues[0].startsWith("don't")) {
            enabled = false
        } else if (match.groupValues[0].startsWith("do")) {
            enabled = true
        }

        index = match.range.last + 1
    }

    return sum
}

fun main() {
    val file = File("inputs/d03.txt")
    val text = file.readText()

    // part 1
    val muls = Mul.parse(text)
    println(Mul.result(muls))

    // part 2
    val result = program(text)
    println(result)
}