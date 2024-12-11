import java.io.File

fun Long.hasEvenDigits() = this.toString().length % 2 == 0
fun Long.split(): Pair<Long, Long> {
    val str = this.toString()
    val str1 = str.substring(0, str.length / 2)
    val str2 = str.substring(str.length / 2)
    return str1.toLong() to str2.toLong()
}

fun MutableMap<Long, Long>.add(value: Long, count: Long) = this.merge(value, count) { a, b -> a + b }

class BlinkingStones1(private val stones: MutableList<Long>) {
    val size get() = stones.size

    fun blink() {
        var index = 0
        while (index < stones.size) {
            val stone = stones[index]

            if (stone == 0L) {
                stones[index] = 1L
            } else if (stone.hasEvenDigits()) {
                val (a, b) = stone.split()
                stones[index] = a
                stones.add(++index, b)
            } else {
                stones[index] *= 2024L
            }

            index++
        }
    }

    companion object {
        fun parse(line: String) = BlinkingStones1(line.split(" ").map { it.toLong() }.toMutableList())
    }
}

class BlinkingStones2(private var stones: MutableMap<Long, Long>) {
    val size get() = stones.map { it.value }.sum()

    fun blink() {
        val newStones = mutableMapOf<Long, Long>()
        for ((stone, count) in stones) {
            if (stone == 0L) {
                newStones.add(1L, count)
            } else if (stone.hasEvenDigits()) {
                val (a, b) = stone.split()
                newStones.add(a, count)
                newStones.add(b, count)
            } else {
                newStones.add(stone * 2024L, count)
            }
        }
        stones = newStones
    }

    companion object {
        fun parse(line: String): BlinkingStones2 {
            val stones = mutableMapOf<Long, Long>()
            for (stone in line.split(" ").map { it.toLong() }) stones.add(stone, 1L)
            return BlinkingStones2(stones)
        }
    }
}

fun main() {
    val file = File("inputs/d11.txt")
    val lines = file.readLines()

    // part 1
    val blinkingStones1 = BlinkingStones1.parse(lines.first())
    repeat(25) { blinkingStones1.blink() }
    println(blinkingStones1.size)

    // part 2
    val blinkingStones2 = BlinkingStones2.parse(lines.first())
    repeat(75) { blinkingStones2.blink() }
    println(blinkingStones2.size)
}