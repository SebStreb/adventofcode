import java.io.File

data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    val rating = x + m + a + s

    companion object {
        fun parse(line: String): Part {
            val data = line.drop(1).dropLast(1).split(",")
            val x = data[0].drop(2).toInt()
            val m = data[1].drop(2).toInt()
            val a = data[2].drop(2).toInt()
            val s = data[3].drop(2).toInt()
            return Part(x, m, a, s)
        }
    }
}

enum class Category { X, M, A, S }
enum class Action { GT, LT }

abstract class Rule(val dest: String, val test: (Part) -> Boolean) {
    companion object {
        fun parse(line: String): Rule {
            if (!line.contains(":")) return EndRule(line)

            val data = line.split(":")
            val dest = data[1]

            val cat = when (data[0].first()) {
                'x' -> Category.X
                'm' -> Category.M
                'a' -> Category.A
                's' -> Category.S
                else -> error("should not happen")
            }
            val action = when (data[0].drop(1).first()) {
                '>' -> Action.GT
                '<' -> Action.LT
                else -> error("should not happen")
            }
            val value = data[0].drop(2).toInt()
            return BranchRule(dest, cat, action, value)
        }
    }
}

class EndRule(dest: String) : Rule(dest, { true })
class BranchRule(dest: String, val cat: Category, val action: Action, val value: Int) :
    Rule(dest, { part ->
        when (cat) {
            Category.X -> when (action) {
                Action.GT -> part.x > value
                Action.LT -> part.x < value
            }

            Category.M -> when (action) {
                Action.GT -> part.m > value
                Action.LT -> part.m < value
            }

            Category.A -> when (action) {
                Action.GT -> part.a > value
                Action.LT -> part.a < value
            }

            Category.S -> when (action) {
                Action.GT -> part.s > value
                Action.LT -> part.s < value
            }
        }
    })

class Workflow(val name: String, val rules: List<Rule>) {

    fun findDest(part: Part): String {
        for (rule in rules) if (rule.test(part)) return rule.dest
        error("should not happen")
    }

    companion object {
        fun parse(line: String): Workflow {
            val data = line.split("{")
            val name = data[0]
            val rules = data[1].dropLast(1).split(",").map { Rule.parse(it) }
            return Workflow(name, rules)
        }
    }
}

class System(val workflows: MutableMap<String, Workflow>, private val parts: MutableList<Part>) {
    val accepted = mutableListOf<Part>()
    private val rejected = mutableListOf<Part>()

    fun run() {
        for (part in parts) {
            var dest = "in"
            while (dest !in listOf("A", "R")) dest = workflows[dest]!!.findDest(part)
            when (dest) {
                "A" -> accepted.add(part)
                "R" -> rejected.add(part)
            }
        }
    }

    companion object {
        fun parse(lines: MutableList<String>): System {
            val workflows = mutableMapOf<String, Workflow>()
            var line = lines.removeFirst()
            while (line.isNotEmpty()) {
                val workflow = Workflow.parse(line)
                workflows[workflow.name] = workflow
                line = lines.removeFirst()
            }

            val parts = mutableListOf<Part>()
            var line2 = lines.removeFirstOrNull()
            while (line2 != null) {
                val part = Part.parse(line2)
                parts.add(part)
                line2 = lines.removeFirstOrNull()
            }

            return System(workflows, parts)
        }
    }
}

fun IntRange.clone() = IntRange(this.first, this.last)
data class RangePart(
    val x: IntRange = IntRange(1, 4000),
    val m: IntRange = IntRange(1, 4000),
    val a: IntRange = IntRange(1, 4000),
    val s: IntRange = IntRange(1, 4000),
) {
    val combinations = x.count().toLong() * m.count().toLong() * a.count().toLong() * s.count().toLong()

    fun branch(rule: BranchRule) = when (rule.cat) {
        Category.X -> when (rule.action) {
            Action.GT -> Pair(
                RangePart(IntRange(rule.value + 1, x.last), m.clone(), a.clone(), s.clone()),
                RangePart(IntRange(x.first, rule.value), m.clone(), a.clone(), s.clone()),
            )

            Action.LT -> Pair(
                RangePart(IntRange(x.first, rule.value - 1), m.clone(), a.clone(), s.clone()),
                RangePart(IntRange(rule.value, x.last), m.clone(), a.clone(), s.clone()),
            )
        }

        Category.M -> when (rule.action) {
            Action.GT -> Pair(
                RangePart(x.clone(), IntRange(rule.value + 1, m.last), a.clone(), s.clone()),
                RangePart(x.clone(), IntRange(m.first, rule.value), a.clone(), s.clone()),
            )

            Action.LT -> Pair(
                RangePart(x.clone(), IntRange(m.first, rule.value - 1), a.clone(), s.clone()),
                RangePart(x.clone(), IntRange(rule.value, m.last), a.clone(), s.clone()),
            )
        }

        Category.A -> when (rule.action) {
            Action.GT -> Pair(
                RangePart(x.clone(), m.clone(), IntRange(rule.value + 1, a.last), s.clone()),
                RangePart(x.clone(), m.clone(), IntRange(a.first, rule.value), s.clone()),
            )

            Action.LT -> Pair(
                RangePart(x.clone(), m.clone(), IntRange(a.first, rule.value - 1), s.clone()),
                RangePart(x.clone(), m.clone(), IntRange(rule.value, a.last), s.clone()),
            )
        }

        Category.S -> when (rule.action) {
            Action.GT -> Pair(
                RangePart(x.clone(), m.clone(), a.clone(), IntRange(rule.value + 1, s.last)),
                RangePart(x.clone(), m.clone(), a.clone(), IntRange(s.first, rule.value)),
            )

            Action.LT -> Pair(
                RangePart(x.clone(), m.clone(), a.clone(), IntRange(s.first, rule.value - 1)),
                RangePart(x.clone(), m.clone(), a.clone(), IntRange(rule.value, s.last)),
            )
        }
    }
}

class System2(private val workflows: Map<String, Workflow>) {
    val accepted = mutableListOf<RangePart>()
    private val rejected = mutableListOf<RangePart>()

    fun run() {
        val waiting = mutableListOf(Pair(RangePart(), workflows["in"]!!))
        while (waiting.isNotEmpty()) {
            val now = waiting.removeFirst()
            var rangePart = now.first
            val workflow = now.second
            for (rule in workflow.rules) {
                when (rule) {
                    is EndRule -> {
                        when (rule.dest) {
                            "A" -> accepted.add(rangePart)
                            "R" -> rejected.add(rangePart)
                            else -> waiting.add(Pair(rangePart, workflows[rule.dest]!!))
                        }
                    }

                    is BranchRule -> {
                        val (ok, ko) = rangePart.branch(rule)
                        when (rule.dest) {
                            "A" -> accepted.add(ok)
                            "R" -> rejected.add(ok)
                            else -> waiting.add(Pair(ok, workflows[rule.dest]!!))
                        }
                        rangePart = ko
                    }
                }
            }
        }
    }
}

fun main() {
    val file = File("inputs/c19")
    val lines = file.readLines()

    // part 1
    val system = System.parse(lines.toMutableList())
    system.run()
    println(system.accepted.sumOf { it.rating })

    // part 2
    val system2 = System2(system.workflows)
    system2.run()
    println(system2.accepted.sumOf { it.combinations })
}