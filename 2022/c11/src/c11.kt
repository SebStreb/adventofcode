import java.io.File

data class Item(var worryLevel: Int)
data class Monkey(
    val id: Int,
    val items: MutableList<Item>,
    val op: (Int) -> Int,
    val test: Int,
    val idTrue: Int,
    val idFalse: Int,
) {
    var inspectedItems = 0

    companion object {
        fun parse(l: List<String>): Monkey {
            val lines = l.toMutableList()

            var line = lines.removeFirst()
            val id = line.drop(7).dropLast(1).toInt()

            line = lines.removeFirst()
            val items = line.drop(18).split(", ").map { Item(it.toInt()) }.toMutableList()

            line = lines.removeFirst()
            val code = line.drop(13)
            val op: (Int) -> Int = when (code) {
                "new = old * 19" -> { x -> x * 19 }
                "new = old + 6" -> { x -> x + 6 }
                "new = old * old" -> { x -> x * x }
                "new = old + 3" -> { x -> x + 3 }

                "new = old * 11" -> { x -> x * 11 }
                "new = old + 4" -> { x -> x + 4 }
                // new = old * old
                "new = old + 2" -> { x -> x + 2 }
                // new = old + 3
                "new = old + 1" -> { x -> x + 1 }
                "new = old + 5" -> { x -> x + 5 }
                // new = old * 19

                else -> error("Op not found")
            }

            line = lines.removeFirst()
            val test = line.drop(21).toInt()

            line = lines.removeFirst()
            val idTrue = line.drop(29).toInt()

            line = lines.removeFirst()
            val idFalse = line.drop(30).toInt()

            return Monkey(id, items, op, test, idTrue, idFalse)
        }
    }
}

open class KeepAway(val monkeys: List<Monkey>) {
    private var roundNumber = 1
    val monkeyBusiness get() = monkeys.map { it.inspectedItems }.sorted().takeLast(2).reduce { a, b -> a * b }

    open fun turn(monkey: Monkey) {
        for (item in monkey.items) {
            monkey.inspectedItems++
            item.worryLevel = monkey.op(item.worryLevel)
            item.worryLevel /= 3
            val testResult = (item.worryLevel % monkey.test) == 0
            val idThrow = if (testResult) monkey.idTrue else monkey.idFalse
            monkeys[idThrow].items.add(item)
        }
        monkey.items.clear()
    }

    private fun round() {
        for (monkey in monkeys) turn(monkey)
        roundNumber++
    }

    open fun game() {
        while (roundNumber <= 20) round()
    }

    companion object {
        fun parse(l: List<String>): KeepAway {
            var lines = l
            val monkeys = mutableListOf<Monkey>()
            while (lines.isNotEmpty()) {
                monkeys += Monkey.parse(lines.take(6))
                lines = lines.drop(7)
            }
            return KeepAway(monkeys)
        }
    }
}

class KeepAway2(monkeys: List<Monkey>) : KeepAway(monkeys) {
    
}

fun main() {
    val file = File("./inputs/c11")
    val lines = file.readLines()

    val keepAway = KeepAway.parse(lines)
    keepAway.game()
    println(keepAway.monkeys.map { it.inspectedItems })
    println(keepAway.monkeyBusiness)
}