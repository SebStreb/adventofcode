import java.io.File

// A rock, B paper, C scissors
// X rock, Y paper, Z scissors
// rock = 1, paper = 2, scissors = 3
val moves1 = mapOf(
    "A X" to 4, // draw + rock
    "A Y" to 8, // win + paper
    "A Z" to 3, // lose + scissors
    "B X" to 1, // lose + rock
    "B Y" to 5, // draw + paper
    "B Z" to 9, // win + scissors
    "C X" to 7, // win + rock
    "C Y" to 2, // lose + paper
    "C Z" to 6, // draw + scissors
)

// X lose, Y draw, Y win
val moves2 = mapOf(
    "A X" to 3, // lose + scissors
    "A Y" to 4, // draw + rock
    "A Z" to 8, // win + paper
    "B X" to 1, // lose + rock
    "B Y" to 5, // draw + paper
    "B Z" to 9, // win + scissors
    "C X" to 2, // lose + paper
    "C Y" to 6, // draw + scissors
    "C Z" to 7, // win + rock
)

fun main() {
    val file = File("./inputs/c02")
    val lines = file.readLines()

    // part 1
    println(lines.sumOf { line -> moves1[line]!! })

    // part 2
    println(lines.sumOf { line -> moves2[line]!! })
}