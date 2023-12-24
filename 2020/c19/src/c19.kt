import java.io.File

var rules = mutableMapOf<Int, Rule>()

abstract class BasicRule {
    abstract fun matchesComplete(string: String): Boolean
}

abstract class Rule : BasicRule() {
    override fun matchesComplete(string: String) = matches(string) == ""
    abstract fun matches(string: String): String?
    abstract fun generate(): Set<String>

    companion object {
        fun parse(lines: List<String>) {
            for (line in lines) {
                val (number, rule) = line.split(": ")
                rules[number.toInt()] = parseOne(rule)
            }
        }

        private fun parseOne(string: String): Rule = when {
            string.contains('|') -> {
                val (first, second) = string.split(" | ")
                OrRule(parseOne(first), parseOne(second))
            }
            string.startsWith('"') -> CharRule(string[1])
            else -> CompositeRule(string.split(" ").map { it.toInt() })
        }
    }
}

class OrRule(private val left: Rule, private val right: Rule) : Rule() {
    override fun matches(string: String) = left.matches(string) ?: right.matches(string)
    override fun generate() = left.generate() + right.generate()
}

class CompositeRule(private val ruleNumbers: List<Int>) : Rule() {
    override fun matches(string: String): String? {
        var remaining: String? = string
        for (number in ruleNumbers) remaining = rules[number]!!.matches(remaining ?: return null)
        return remaining
    }

    override fun generate(): Set<String> {
        var res = setOf("")
        for (number in ruleNumbers) {
            val rule = rules[number]!!
            res = res.flatMap { s -> rule.generate().map { s + it } }.toSet()
        }
        return res
    }
}

class CharRule(private val match: Char) : Rule() {
    override fun matches(string: String) = if (string.startsWith(match)) string.drop(1) else null
    override fun generate() = setOf(match.toString())
}

class Rule42(private val matches: Set<String>) : Rule() {
    override fun matches(string: String): String? {
        for (match in matches) if (string.startsWith(match)) return string.drop(match.length)
        return null
    }

    override fun generate() = matches
}

class Rule31(private val matches: Set<String>) : Rule() {
    override fun matches(string: String): String? {
        for (match in matches) if (string.endsWith(match)) return string.dropLast(match.length)
        return null
    }

    override fun generate() = matches
}

class Rule11(private val rule42: Rule42, private val rule31: Rule31) : BasicRule() {
    override fun matchesComplete(string: String): Boolean {
        var remaining = rule42.matches(string) ?: return false
        remaining = rule31.matches(remaining) ?: return false
        return if (remaining.isEmpty()) true else matchesComplete(remaining)
    }
}

class Rule0(private val rule42: Rule42, private val rule11: Rule11) : BasicRule() {
    override fun matchesComplete(string: String): Boolean {
        var remaining: String = string
        while (remaining.isNotEmpty()) {
            remaining = rule42.matches(remaining) ?: return false
            if (rule11.matchesComplete(remaining)) return true
        }
        return false
    }
}

class Rules(private val rule0: BasicRule, private val messages: List<String>) {
    fun countMatches() = messages.count { rule0.matchesComplete(it) }

    fun newRules(): Rules {
        val rule31 = Rule31(rules[31]!!.generate())
        val rule42 = Rule42(rules[42]!!.generate())
        val rule11 = Rule11(rule42, rule31)
        val rule0 = Rule0(rule42, rule11)
        return Rules(rule0, messages)
    }

    companion object {
        fun parse(lines: List<String>): Rules {
            val rulesLines = lines.takeWhile { it.isNotEmpty() }
            val messages = lines.dropWhile { it.isNotEmpty() }.drop(1)
            Rule.parse(rulesLines)
            return Rules(rules[0]!!, messages)
        }
    }
}

fun main() {
    val file = File("inputs/c19")
    val lines = file.readLines()

    // part 1
    val rules1 = Rules.parse(lines)
    println(rules1.countMatches())

    // part 2
    val rules2 = rules1.newRules()
    println(rules2.countMatches())
}