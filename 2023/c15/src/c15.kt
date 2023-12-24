import java.io.File

fun HASH(string: String): Int {
    var hash = 0
    for (i in string.indices) {
        hash += string[i].code
        hash *= 17
        hash %= 256
    }
    return hash
}

data class Lens(val label: String, var length: Int) {
    fun focusingPower(rank: Int) = (rank * length).toLong()
}
class Box(private val id: Int) {
    val lenses = mutableListOf<Lens>()
    fun focusingPower() = lenses.mapIndexed { i, lens -> (id + 1) * lens.focusingPower(i + 1) }.sum()
}
val boxes = List(256) { Box(it) }

open class Op(val label: String)
class Add(label: String, val value: Int): Op(label)
class Remove(label: String): Op(label)

fun parse(step: String): Op {
    if (step.contains("=")) {
        val (label, op) = step.split("=")
        return Add(label, op.toInt())
    } else {
        val label = step.dropLast(1)
        return Remove(label)
    }
}

fun exec(op: Op) {
    when (op) {
        is Add -> {
            val box = boxes[HASH(op.label)]
            if (box.lenses.any { it.label == op.label }) {
                box.lenses.first { it.label == op.label }.length = op.value
            } else box.lenses.add(Lens(op.label, op.value))
        }
        is Remove -> {
            val box = boxes[HASH(op.label)]
            box.lenses.removeAll { it.label == op.label }
        }
    }
}

fun main() {
    val file = File("./inputs/c15")
    val lines = file.readLines()

    // part 1
    val steps = lines.first().split(",")
    println(steps.sumOf { HASH(it) })

    // part 2
    for (step in steps) {
        val op = parse(step)
        exec(op)
    }
    println(boxes.sumOf { it.focusingPower() })
}