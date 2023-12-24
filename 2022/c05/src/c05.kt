import java.io.File
import java.util.Stack

data class Move(val count: Int, val from: Int, val to: Int)

fun createStacks(): List<Stack<String>> {
    val stacks = mutableListOf<Stack<String>>()

    val stack0 = Stack<String>()
    stacks.add(stack0)

    val stack1 = Stack<String>()
    stack1.push("Q")
    stack1.push("W")
    stack1.push("P")
    stack1.push("S")
    stack1.push("Z")
    stack1.push("R")
    stack1.push("H")
    stack1.push("D")
    stacks.add(stack1)

    val stack2 = Stack<String>()
    stack2.push("V")
    stack2.push("B")
    stack2.push("R")
    stack2.push("W")
    stack2.push("Q")
    stack2.push("H")
    stack2.push("F")
    stacks.add(stack2)

    val stack3 = Stack<String>()
    stack3.push("C")
    stack3.push("V")
    stack3.push("S")
    stack3.push("H")
    stacks.add(stack3)

    val stack4 = Stack<String>()
    stack4.push("H")
    stack4.push("F")
    stack4.push("G")
    stacks.add(stack4)

    val stack5 = Stack<String>()
    stack5.push("P")
    stack5.push("G")
    stack5.push("J")
    stack5.push("B")
    stack5.push("Z")
    stacks.add(stack5)

    val stack6 = Stack<String>()
    stack6.push("Q")
    stack6.push("T")
    stack6.push("J")
    stack6.push("H")
    stack6.push("W")
    stack6.push("F")
    stack6.push("L")
    stacks.add(stack6)

    val stack7 = Stack<String>()
    stack7.push("Z")
    stack7.push("T")
    stack7.push("W")
    stack7.push("D")
    stack7.push("L")
    stack7.push("V")
    stack7.push("J")
    stack7.push("N")
    stacks.add(stack7)

    val stack8 = Stack<String>()
    stack8.push("D")
    stack8.push("T")
    stack8.push("Z")
    stack8.push("C")
    stack8.push("J")
    stack8.push("G")
    stack8.push("H")
    stack8.push("F")
    stacks.add(stack8)

    val stack9 = Stack<String>()
    stack9.push("W")
    stack9.push("P")
    stack9.push("V")
    stack9.push("M")
    stack9.push("B")
    stack9.push("H")
    stacks.add(stack9)

    return stacks
}

fun decode(line: String): Move {
    val data = line.split(" ")
    return Move(data[1].toInt(), data[3].toInt(), data[5].toInt())
}

fun execute(move: Move, stacks: List<Stack<String>>) {
    repeat(move.count) {
        val crate = stacks[move.from].pop()
        stacks[move.to].push(crate)
    }
}

fun execute2(move: Move, stacks: List<Stack<String>>) {
    val crates = mutableListOf<String>()
    repeat(move.count) {
        val crate = stacks[move.from].pop()
        crates.add(crate)
    }
    crates.reversed().forEach { crate -> stacks[move.to].push(crate) }
}

fun main() {
    val stacks = createStacks()
    val file = File("./inputs/c05")
    val lines = file.readLines().drop(10)
    lines.forEach { execute2(decode(it), stacks) }
    for (stack in stacks.drop(1)) print(stack.pop())
    println()
}
