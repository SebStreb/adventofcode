import java.io.File
import java.util.*

enum class Pulse { LOW, HIGH, NONE }

data class Signal(val from: String, val to: String, val pulse: Pulse)

abstract class Module(val name: String) {
    val outputs = mutableListOf<Module>()
    abstract val state: String

    fun registerOutput(output: Module) {
        outputs.add(output)
    }

    open fun run(input: Pulse, from: String): Pulse {
        register(input)
        return compute(input)
    }

    abstract fun compute(input: Pulse): Pulse

    companion object {
        var lowInputs = 0
        var highInputs = 0

        fun parse(lines: List<String>): Map<String, Module> {
            val modules = mutableMapOf<String, Module>()

            modules[Button.NAME] = Button()

            lines.forEach { line ->
                val (from, _) = line.split(" -> ")

                when {
                    from.startsWith(Broadcast.NAME) -> modules[Broadcast.NAME] = Broadcast()

                    from.startsWith(FlipFlop.PREFIX) -> {
                        val name = from.drop(1)
                        modules[name] = FlipFlop(name)
                    }

                    from.startsWith(Conjunction.PREFIX) -> {
                        val name = from.drop(1)
                        modules[name] = Conjunction(name)
                    }
                }
            }

            val button = modules[Button.NAME]!!
            val broadcast = modules[Broadcast.NAME]!!
            button.registerOutput(broadcast)

            lines.forEach { line ->
                val (from, to) = line.split(" -> ")

                val name = if (from.startsWith(Conjunction.PREFIX) || from.startsWith(FlipFlop.PREFIX)) {
                    from.drop(1)
                } else from
                val fromModule = modules[name]!!

                val outputs = to.split(", ")
                for (output in outputs) {
                    if (output !in modules) modules[output] = Output(output)
                    val toModule = modules[output]!!
                    fromModule.registerOutput(toModule)
                    if (toModule is Conjunction) toModule.registerInput(name)
                }
            }

            return modules
        }

        fun register(pulse: Pulse) {
            when (pulse) {
                Pulse.LOW -> lowInputs++
                Pulse.HIGH -> highInputs++
                Pulse.NONE -> {}
            }
        }
    }
}

class Button : Module(NAME) {
    override val state = NAME

    override fun compute(input: Pulse) = Pulse.LOW

    companion object {
        const val NAME = "button"
    }
}

class Broadcast : Module(NAME) {
    override val state = NAME

    override fun compute(input: Pulse) = input

    companion object {
        const val NAME = "broadcaster"
    }
}

class FlipFlop(name: String) : Module(name) {
    override val state get() = if (active) "active" else "inactive"
    private var active = false

    override fun compute(input: Pulse): Pulse {
        if (input == Pulse.HIGH) return Pulse.NONE
        active = !active
        return if (active) Pulse.HIGH else Pulse.LOW
    }

    companion object {
        const val PREFIX = "%"
    }
}

class Conjunction(name: String) : Module(name) {
    override val state get() = inputs.entries.joinToString(",") { (name, pulse) -> "$name:$pulse" }
    val inputs = mutableMapOf<String, Pulse>()

    fun registerInput(input: String) {
        inputs[input] = Pulse.LOW
    }

    override fun run(input: Pulse, from: String): Pulse {
        inputs[from] = input
        return super.run(input, from)
    }

    override fun compute(input: Pulse) = if (inputs.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH

    companion object {
        const val PREFIX = "&"
    }
}

class Output(name: String) : Module(name) {
    override val state = NAME

    override fun compute(input: Pulse) = Pulse.NONE

    companion object {
        const val NAME = "output"
    }
}

val Map<String, Module>.state get() = values.joinToString("\n") { "${it.name}=${it.state}" }

fun part1(lines: List<String>) {
    val modules = Module.parse(lines)

    var cycleLength = 0
    val firstState = modules.state

    do {
        val open = mutableListOf(Signal(Button.NAME, Broadcast.NAME, Pulse.LOW))
        while (open.isNotEmpty()) {
            val signal = open.removeFirst()
            val to = modules[signal.to]!!
            val pulse = to.run(signal.pulse, signal.from)
            if (pulse != Pulse.NONE) for (output in to.outputs) open.add(Signal(to.name, output.name, pulse))
        }

        cycleLength++
        val currentState = modules.state
    } while (currentState != firstState && cycleLength < 1000)

    val lowInputs = Module.lowInputs * 1000 / cycleLength
    val highInputs = Module.highInputs * 1000 / cycleLength

    println(lowInputs * highInputs)
}

fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)

fun lcm(vararg numbers: Long) = Arrays.stream(numbers).reduce(1) { x, y -> x * (y / gcd(x, y)) }

fun part2(lines: List<String>) {
    var modules = Module.parse(lines)

    // dg: conjunction sending to rx
    // dg sends LOW to rx when all inputs are HIGH
    val dg = modules["dg"]!! as Conjunction
    val dgInputs = dg.inputs.keys

    val cycles = dgInputs.map { input ->
        modules = Module.parse(lines)
        val module = modules[input]!!

        var done = false
        var cycleLength = 0L

        while (!done) {
            val open = mutableListOf(Signal(Button.NAME, Broadcast.NAME, Pulse.LOW))
            cycleLength++

            while (open.isNotEmpty()) {
                val signal = open.removeFirst()

                if (signal.from == module.name && signal.to == dg.name && signal.pulse == Pulse.HIGH) done = true

                val to = modules[signal.to]!!
                val pulse = to.run(signal.pulse, signal.from)
                if (pulse != Pulse.NONE) for (output in to.outputs) open.add(Signal(to.name, output.name, pulse))
            }
        }

        cycleLength
    }

    println(lcm(*cycles.toLongArray()))
}

fun main() {
    val file = File("inputs/c20")
    val lines = file.readLines()

    // part 1
    part1(lines)

    // part 2
    part2(lines)
}