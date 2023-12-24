import java.io.File
import kotlin.math.pow

data class Card(val winning: List<Int>, val numbers: List<Int>) {
    val count = numbers.count { winning.contains(it) }
    val points = if (count == 0) 0 else 2.0.pow(count - 1).toInt()
    var copies = 1
}

class Bag(private val cards: List<Card>) {
    val totalPoints = cards.sumOf { it.points }
    val size get() = cards.sumOf { it.copies }

    fun expand() {
        for ((index, card) in cards.withIndex())
            for (nextCard in cards.drop(index + 1).take(card.count))
                nextCard.copies += card.copies
    }

    companion object {
        fun parse(lines: List<String>): Bag {
            val cards = lines.map { line ->
                val data = line.split(": ")[1].split(" | ")
                val winning = data[0].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
                val numbers = data[1].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
                Card(winning, numbers)
            }
            return Bag(cards)
        }
    }
}

fun main() {
    val file = File("./inputs/c04")
    val lines = file.readLines()

    val bag = Bag.parse(lines)

    // Part 1
    println(bag.totalPoints)

    // Part 2
    bag.expand()
    println(bag.size)
}