import java.io.File
import java.util.*

class Node(val label: String) {
    lateinit var left: Node
    lateinit var right: Node
}

fun getInstructions(line: String) = line.toCharArray().toList()
fun getNodes(lines: List<String>): List<Node> {
    val nodes = lines.map { Node(it.take(3)) }
    for ((index, line) in lines.withIndex()) {
        val node = nodes[index]
        val data = line.drop(7).dropLast(1).split(", ")
        val left = nodes.first { it.label == data[0] }
        val right = nodes.first { it.label == data[1] }
        node.left = left
        node.right = right
    }
    return nodes
}

class Network1(private val instructions: List<Char>, private val start: Node) {
    var steps = 0

    fun walkThrough() {
        var current = start
        while (current.label != "ZZZ") {
            when (instructions[steps % instructions.size]) {
                'L' -> current = current.left
                'R' -> current = current.right
            }
            steps++
        }
    }

    companion object {
        fun parse(lines: List<String>): Network1 {
            val instructions = getInstructions(lines.first())
            val nodes = getNodes(lines.drop(2))
            return Network1(instructions, nodes.first { it.label == "AAA" })
        }
    }
}

class Network2(private val instructions: List<Char>, private val start: Node) {
    var steps = 0L

    fun walkThrough() {
        var current = start
        while (!current.label.endsWith('Z')) {
            when (instructions[(steps % instructions.size).toInt()]) {
                'L' -> current = current.left
                'R' -> current = current.right
            }
            steps++
        }
    }

    companion object {
        fun parse(lines: List<String>): List<Network2> {
            val instructions = getInstructions(lines.first())
            val nodes = getNodes(lines.drop(2))
            return nodes.filter { it.label.endsWith('A') }.map { Network2(instructions, it) }
        }
    }
}

fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)

fun lcm(vararg numbers: Long) = Arrays.stream(numbers).reduce(1) { x, y -> x * (y / gcd(x, y)) }

fun main() {
    val file = File("./inputs/c08")
    val lines = file.readLines()

    // part 1
    val network = Network1.parse(lines)
    network.walkThrough()
    println(network.steps)

    // part 2
    val networks = Network2.parse(lines)
    networks.forEach { it.walkThrough() }
    val steps = networks.map { it.steps }
    println(lcm(*steps.toLongArray()))
}