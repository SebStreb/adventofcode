import java.io.File

open class Instruction(val cycles: Int)
class Add(val value: Int) : Instruction(2)
class Noop : Instruction(1)

fun parse(lines: List<String>) = lines.map { line ->
    if (line == "noop") Noop()
    else Add(line.drop(5).toInt())
}

class CPU(private var instructions: List<Instruction>) {
    private var xValue = 1
    private var cycle = 0
        set(value) {
            field = value
            if (cycle in listOf(20, 60, 100, 140, 180, 220)) signalStrengths += Pair(cycle, xValue)
        }
    val signalStrengths = mutableListOf<Pair<Int, Int>>()

    private fun executeNext() {
        val instruction = instructions.first()
        instructions = instructions.drop(1)

        repeat(instruction.cycles) { cycle += 1 }
        when (instruction) {
            is Add -> xValue += instruction.value
            is Noop -> {}
        }
    }

    fun executeAll() {
        while (instructions.isNotEmpty()) executeNext()
    }
}

class CPU2(private var instructions: List<Instruction>) {
    private var xValue = 1
    private var cycle = 1
    private var busy = false
    var drawing = ""

    private fun drawAPixel() {
        val row = (cycle - 1) % 40
        drawing += if (row < xValue - 1 || row > xValue + 1) "." else "#"
        if (row == 39) drawing += "\n"
    }

    private fun doACycle() {
        val instruction = instructions.first()

        drawAPixel()

        if (instruction is Add) {
            if (busy) {
                xValue += instruction.value
                instructions = instructions.drop(1)
                busy = false
            } else busy = true
        } else instructions = instructions.drop(1)
        cycle++
    }

    fun draw() = repeat(240) { doACycle() }
}

fun main() {
    val file = File("./inputs/c10")
    val lines = file.readLines()

    // part 1
    val cpu = CPU(parse(lines))
    cpu.executeAll()
    println(cpu.signalStrengths.sumOf { it.first * it.second })

    // part 2
    val cpu2 = CPU2(parse(lines))
    cpu2.draw()
    println(cpu2.drawing)
}