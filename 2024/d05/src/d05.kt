import java.io.File

data class OrderingRule(val before: Int, val after: Int) {
    companion object {
        // No total order exists unfortunately, dead code left for reference
        fun totalOrder(rules: List<OrderingRule>): List<Int> {
            val order = mutableListOf<Int>()
            val remainingPages = rules.flatMap { listOf(it.before, it.after) }.toMutableSet()

            while (remainingPages.isNotEmpty()) {
                val page = remainingPages.random()

                var index = 0
                while (index < order.size && rules.any { it.after == page && order.drop(index).contains(it.before) }) {
                    index++
                }
                order.add(index, page)

                remainingPages.remove(page)
            }

            return order
        }

        fun parse(line: String): OrderingRule {
            val (before, after) = line.split("|").map { it.toInt() }
            return OrderingRule(before, after)
        }
    }
}

data class PrintOrder(val pages: List<Int>) {
    val value = pages[pages.size / 2]

    private fun findSwap(rules: List<OrderingRule>): Pair<Int, Int>? {
        for (index in pages.indices) {
            val currentPage = pages[index]
            for (rule in rules) {
                if (rule.after == currentPage && pages.drop(index).contains(rule.before)) {
                    return Pair(index, pages.indexOf(rule.before))
                }
            }
        }
        return null
    }

    fun isValid(rules: List<OrderingRule>) = findSwap(rules) == null

    fun reorder(rules: List<OrderingRule>): PrintOrder {
        val newPages = pages.toMutableList()
        while (!PrintOrder(newPages).isValid(rules)) {
            val (beforeIndex, afterIndex) = PrintOrder(newPages).findSwap(rules) ?: break
            val mem = newPages[beforeIndex]
            newPages[beforeIndex] = newPages[afterIndex]
            newPages[afterIndex] = mem
        }
        return PrintOrder(newPages)
    }

    companion object {
        fun parse(line: String) = PrintOrder(line.split(",").map { it.toInt() })
    }
}

fun parseInput(lines: List<String>): Pair<List<OrderingRule>, List<PrintOrder>> {
    val emptyLineIndex = lines.indexOf("")
    val rulesLines = lines.subList(0, emptyLineIndex)
    val ordersLines = lines.subList(emptyLineIndex + 1, lines.size)
    val rules = rulesLines.map { OrderingRule.parse(it) }
    val orders = ordersLines.map { PrintOrder.parse(it) }
    return Pair(rules, orders)
}

fun part1(rules: List<OrderingRule>, orders: List<PrintOrder>) = orders.filter { it.isValid(rules) }.sumOf { it.value }

fun part2(rules: List<OrderingRule>, orders: List<PrintOrder>) =
    orders.filter { !it.isValid(rules) }.sumOf { it.reorder(rules).value }

fun main() {
    val file = File("inputs/d05.txt")
    val lines = file.readLines()
    val (rules, orders) = parseInput(lines)

    // part 1
    println(part1(rules, orders))

    // part 2
    println(part2(rules, orders))
}