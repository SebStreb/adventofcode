import java.io.File

data class EquationTree(private val operands: List<Long>) {
    fun isPossible(result: Long): Boolean {
        if (operands.size == 1) return operands[0] == result

        val x = operands[0]
        val y = operands[1]
        val rest = operands.drop(2)

        return EquationTree(listOf(x + y) + rest).isPossible(result) ||
                EquationTree(listOf(x * y) + rest).isPossible(result)
    }

    fun isPossible2(result: Long): Boolean {
        if (operands.size == 1) return operands[0] == result

        val x = operands[0]
        val y = operands[1]
        val rest = operands.drop(2)

        val value1 = x + y
        val value2 = x * y
        val value3 = (x.toString() + y.toString()).toLong()

        return EquationTree(listOf(value1) + rest).isPossible2(result) ||
                EquationTree(listOf(value2) + rest).isPossible2(result) ||
                EquationTree(listOf(value3) + rest).isPossible2(result)
    }
}

data class Equation(val operands: List<Long>, val result: Long) {
    fun isPossible() = EquationTree(operands).isPossible(result)
    fun isPossible2() = EquationTree(operands).isPossible2(result)

    companion object {
        private fun parseOne(line: String): Equation {
            val (result, operands) = line.split(": ")
            return Equation(operands.split(" ").map { it.toLong() }, result.toLong())
        }

        fun parse(lines: List<String>) = lines.map { parseOne(it) }
    }
}

fun main() {
    val file = File("inputs/d07.txt")
    val lines = file.readLines()
    val equations = Equation.parse(lines)

    // part 1
    println(equations.filter { it.isPossible() }.sumOf { it.result })

    // part 2
    println(equations.filter { it.isPossible2() }.sumOf { it.result })
}