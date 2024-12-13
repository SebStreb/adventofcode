import java.io.File

data class Prize(val x: Long, val y: Long) {
    companion object {
        private val regex = """X=(\d+), Y=(\d+)""".toRegex()
        fun parse(line: String): Prize {
            val (x, y) = regex.find(line)!!.destructured
            return Prize(x.toLong(), y.toLong())
        }
    }
}

data class Button(val x: Long, val y: Long) {
    companion object {
        private val regex = """X\+(\d+), Y\+(\d+)""".toRegex()
        fun parse(line: String): Button {
            val (x, y) = regex.find(line)!!.destructured
            return Button(x.toLong(), y.toLong())
        }
    }
}

data class Machine(private val buttonA: Button, private val buttonB: Button, private val prize1: Prize) {
    private val prize2 = Prize(prize1.x + 10000000000000, prize1.y  + 10000000000000)

    fun findCoefficients1(): Pair<Int, Int>? {
        for (a in 0..100) {
            for (b in 0..100) {
                if (prize1.x == a * buttonA.x + b * buttonB.x && prize1.y == a * buttonA.y + b * buttonB.y) return a to b
            }
        }
        return null
    }

    fun findCoefficients2(): Pair<Long, Long>? {
        if (buttonA.x * buttonB.y == buttonB.x * buttonA.y) return null // can't divide by zero
        val a = (prize2.x * buttonB.y - buttonB.x * prize2.y).toDouble() / (buttonA.x * buttonB.y - buttonB.x * buttonA.y).toDouble()
        val b = (prize2.y * buttonA.x - prize2.x * buttonA.y).toDouble() / (buttonA.x * buttonB.y - buttonB.x * buttonA.y).toDouble()
        if (a % 1 != 0.0 || b % 1 != 0.0) return null // not integer values
        return a.toLong() to b.toLong()
    }

    companion object {
        private fun parseOne(lines: List<String>) =
            Machine(Button.parse(lines[0]), Button.parse(lines[1]), Prize.parse(lines[2]))

        fun parseAll(lines: List<String>): List<Machine> {
            val machines = mutableListOf<Machine>()
            var remainingLines = lines
            while (remainingLines.isNotEmpty()) {
                machines.add(parseOne(remainingLines.take(3)))
                remainingLines = remainingLines.drop(4)
            }
            return machines
        }
    }
}

fun part1(machines: List<Machine>) = machines.mapNotNull { it.findCoefficients1() }.sumOf { it.first * 3 + it.second }
fun part2(machines: List<Machine>) = machines.mapNotNull { it.findCoefficients2() }.sumOf { it.first * 3 + it.second }

fun main() {
    val file = File("inputs/d13.txt")
    val lines = file.readLines()

    val machines = Machine.parseAll(lines)

    // part 1
    println(part1(machines))

    // part 2
    println(part2(machines))
}