import java.io.File

enum class HandType(val value: Int) {
    FIVE_OF_A_KIND(7), FOUR_OF_A_KIND(6), FULL_HOUSE(5),
    THREE_OF_A_KIND(4), TWO_PAIR(3), PAIR(2), HIGH(1)
}

enum class Card(val label: Char, val value: Int) {
    ACE('A', 14), KING('K', 13), QUEEN('Q', 12), JACK('J', 11),
    TEN('T', 10), NINE('9', 9), EIGHT('8', 8), SEVEN('7', 7),
    SIX('6', 6), FIVE('5', 5), FOUR('4', 4), THREE('3', 3),
    TWO('2', 2)
}

class Hand(private val cards: List<Card>, val bid: Int) : Comparable<Hand> {
    private val type: HandType = findType()

    private fun findType(): HandType {
        val map = mutableMapOf<Card, Int>()
        cards.forEach { card -> map[card] = map.getOrDefault(card, 0) + 1 }

        return if (map.containsValue(5)) HandType.FIVE_OF_A_KIND
        else if (map.containsValue(4)) HandType.FOUR_OF_A_KIND
        else if (map.containsValue(3) && map.containsValue(2)) HandType.FULL_HOUSE
        else if (map.containsValue(3)) HandType.THREE_OF_A_KIND
        else if (map.count { entry -> entry.value == 2 } == 2) HandType.TWO_PAIR
        else if (map.containsValue(2)) HandType.PAIR
        else HandType.HIGH
    }

    override fun compareTo(other: Hand): Int =
        if (type.value != other.type.value) type.value - other.type.value
        else {
            var cardIndex = 0
            while (cardIndex < 5 && cards[cardIndex] == other.cards[cardIndex]) cardIndex++
            if (cardIndex == 5) 0
            else cards[cardIndex].value - other.cards[cardIndex].value
        }

    companion object {
        fun parse(lines: List<String>) = lines.map { line ->
            val data = line.split(" ")
            val cards = mutableListOf<Card>()
            data[0].forEach { char -> cards += Card.entries.first { it.label == char } }
            val bid = data[1].toInt()
            Hand(cards, bid)
        }
    }
}


enum class Card2(val label: Char, val value: Int) {
    ACE('A', 14), KING('K', 13), QUEEN('Q', 12), TEN('T', 10),
    NINE('9', 9), EIGHT('8', 8), SEVEN('7', 7), SIX('6', 6),
    FIVE('5', 5), FOUR('4', 4), THREE('3', 3), TWO('2', 2),
    JOKER('J', 1)
}

class Hand2(private val cards: List<Card2>, val bid: Int) : Comparable<Hand2> {
    private val type: HandType = findType()

    private fun findType(): HandType {
        val map = mutableMapOf<Card2, Int>()
        cards.forEach { card -> map[card] = map.getOrDefault(card, 0) + 1 }
        val jokers = map.getOrDefault(Card2.JOKER, 0)

        return if (map.containsValue(5)) HandType.FIVE_OF_A_KIND
        else if (map.containsValue(4) && jokers == 1) HandType.FIVE_OF_A_KIND
        else if (map.containsValue(3) && jokers == 2) HandType.FIVE_OF_A_KIND
        else if (map.containsValue(2) && jokers == 3) HandType.FIVE_OF_A_KIND
        else if (map.containsValue(1) && jokers == 4) HandType.FIVE_OF_A_KIND
        else if (map.containsValue(4)) HandType.FOUR_OF_A_KIND
        else if (map.containsValue(3) && jokers == 1) HandType.FOUR_OF_A_KIND
        else if (jokers == 2 && map.count { entry -> entry.value == 2 } == 2) HandType.FOUR_OF_A_KIND
        else if (map.containsValue(1) && jokers == 3) HandType.FOUR_OF_A_KIND
        else if (map.containsValue(3) && map.containsValue(2)) HandType.FULL_HOUSE
        else if (map.count { entry -> entry.value == 2 } == 2 && jokers == 1) HandType.FULL_HOUSE
        else if (map.containsValue(3)) HandType.THREE_OF_A_KIND
        else if (map.containsValue(2) && jokers == 1) HandType.THREE_OF_A_KIND
        else if (map.containsValue(1) && jokers == 2) HandType.THREE_OF_A_KIND
        else if (map.count { entry -> entry.value == 2 } == 2) HandType.TWO_PAIR
        else if (map.containsValue(2)) HandType.PAIR
        else if (map.containsValue(1) && jokers == 1) HandType.PAIR
        else HandType.HIGH
    }

    override fun compareTo(other: Hand2): Int =
        if (type.value != other.type.value) type.value - other.type.value
        else {
            var cardIndex = 0
            while (cardIndex < 5 && cards[cardIndex] == other.cards[cardIndex]) cardIndex++
            if (cardIndex == 5) 0
            else cards[cardIndex].value - other.cards[cardIndex].value
        }

    companion object {
        fun parse(lines: List<String>) = lines.map { line ->
            val data = line.split(" ")
            val cards = mutableListOf<Card2>()
            data[0].forEach { char -> cards += Card2.entries.first { it.label == char } }
            val bid = data[1].toInt()
            Hand2(cards, bid)
        }
    }
}


fun main() {
    val file = File("./inputs/c07")
    val lines = file.readLines()

    // part1
    val hands = Hand.parse(lines)
    val result = hands.sorted().withIndex().sumOf { (index, hand) -> hand.bid * (index + 1) }
    println(result)

    // part2
    val hands2 = Hand2.parse(lines)
    val result2 = hands2.sorted().withIndex().sumOf { (index, hand) -> hand.bid * (index + 1) }
    println(result2)
}