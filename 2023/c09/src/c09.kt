import java.io.File

class Sequence(private val values: List<Int>) {
    private val child =
        if (values.all { it == 0 }) null
        else Sequence((1..<values.size).map { i -> values[i] - values[i - 1] })

    fun genNext(): Int = if (child == null) 0 else values.last() + child.genNext()

    fun genPrevious(): Int = if (child == null) 0 else values.first() - child.genPrevious()

    companion object {
        private fun parseOne(line: String) = Sequence(line.split(" ").map { it.toInt() })
        fun parse(lines: List<String>) = lines.map { parseOne(it) }
    }
}

fun main() {
    val file = File("./inputs/c09")
    val lines = file.readLines()

    // part 1
    val sequences = Sequence.parse(lines)
    println(sequences.sumOf { it.genNext() })

    // part 2
    println(sequences.sumOf { it.genPrevious() })
}