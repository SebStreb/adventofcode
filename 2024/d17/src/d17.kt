import java.io.File
import kotlin.math.pow

fun Long.pow(value: Long) = this.toDouble().pow(value.toDouble()).toLong()

data class Operand(val literal: Long) {
    fun combo(memory: Memory) = when (literal) {
        0L -> 0
        1L -> 1
        2L -> 2
        3L -> 3
        4L -> memory.a
        5L -> memory.b
        6L -> memory.c
        else -> throw IllegalArgumentException("Invalid operand: $literal")
    }
}

abstract class Operation(val operand: Operand) {
    open fun apply(computer: Computer) = computer.ip + 1
}

class adv(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.memory.a /= 2L.pow(operand.combo(computer.memory))
        return super.apply(computer)
    }
}

class bxl(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.memory.b = computer.memory.b xor operand.literal
        return super.apply(computer)
    }
}

class bst(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.memory.b = operand.combo(computer.memory) % 8
        return super.apply(computer)
    }
}

class jnz(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer) = if (computer.memory.a != 0L) operand.literal else super.apply(computer)
}

class bxc(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.memory.b = computer.memory.b xor computer.memory.c
        return super.apply(computer)
    }
}

class out(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.output.add(operand.combo(computer.memory) % 8)
        return super.apply(computer)
    }
}

class bdv(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.memory.b = computer.memory.a / 2L.pow(operand.combo(computer.memory))
        return super.apply(computer)
    }
}

class cdv(operand: Operand) : Operation(operand) {
    override fun apply(computer: Computer): Long {
        computer.memory.c = computer.memory.a / 2L.pow(operand.combo(computer.memory))
        return super.apply(computer)
    }
}

data class Memory(
    var a: Long = 0,
    var b: Long = 0,
    var c: Long = 0,
)


data class Computer(private val program: List<Long>, private val ops: List<Operation>, val memory: Memory) {
    val output = mutableListOf<Long>()
    var ip = 0L

    fun compute(): String {
        while (ip in ops.indices) ip = ops[ip.toInt()].apply(this)
        return output.joinToString(",")
    }

    private fun find(acc: Long, digits: Int): List<Long> {
        if (digits == 16) return listOf(acc)
        val expected = program.takeLast(digits).joinToString(",")
        val found = mutableListOf<Long>()
        for (value in acc..(acc + 8)) {
            val computer = Computer(program, ops, Memory(a = value))
            if (computer.compute() == expected) found += computer.find(value * 8, digits + 1)
        }
        return found
    }

    fun find() = find(0, 1).min()

    companion object {
        fun parse(lines: List<String>): Computer {
            val a = lines[0].split(": ")[1].toLong()
            val b = lines[1].split(": ")[1].toLong()
            val c = lines[2].split(": ")[1].toLong()
            val memory = Memory(a, b, c)

            val ops = mutableListOf<Operation>()
            val program = lines[4].split(": ")[1].split(",").map { it.toLong() }
            for (i in program.indices step 2) {
                val opCode = program[i]
                val operand = Operand(program[i + 1])
                ops += when (opCode) {
                    0L -> adv(operand)
                    1L -> bxl(operand)
                    2L -> bst(operand)
                    3L -> jnz(operand)
                    4L -> bxc(operand)
                    5L -> out(operand)
                    6L -> bdv(operand)
                    7L -> cdv(operand)
                    else -> throw IllegalArgumentException("Invalid opcode: ${program[i]}")
                }
            }

            return Computer(program, ops, memory)
        }
    }
}

fun main() {
    val file = File("inputs/d17.txt")
    val lines = file.readLines()
    val computer = Computer.parse(lines)

    // part 1
    println(computer.compute())

    // part 2
    println(computer.find())
}