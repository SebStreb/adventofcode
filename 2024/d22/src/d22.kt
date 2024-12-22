import java.io.File

fun mix(value: Long, secret: Long) = value xor secret
fun prune(secret: Long) = secret.mod(16777216).toLong()

fun next(previousSecret: Long): Long {
    var result: Long
    var secret = previousSecret

    result = secret * 64
    secret = mix(result, secret)
    secret = prune(secret)

    result = secret / 32
    secret = mix(result, secret)
    secret = prune(secret)

    result = secret * 2048
    secret = mix(result, secret)
    secret = prune(secret)

    return secret
}

fun part1(lines: List<String>): Long {
    var sum = 0L

    for (line in lines) {
        var secret = line.toLong()
        repeat(2000) { secret = next(secret) }
        sum += secret
    }

    return sum
}

fun getPrice(secret: Long) = secret.mod(10)

data class Sequence(val change1: Int, val change2: Int, val change3: Int, val change4: Int)

data class Monkey(private val initialSecret: Long) {
    val prices = mutableMapOf<Sequence, Int>()

    init {
        val initialPrice = getPrice(initialSecret)
        val priceChanges = mutableListOf<Int>()
        var secret = initialSecret
        var lastPrice = initialPrice
        repeat(2000) {
            secret = next(secret)
            val price = getPrice(secret)
            priceChanges += price - lastPrice
            lastPrice = price

            if (priceChanges.size >= 4) {
                val (change1, change2, change3, change4) = priceChanges.takeLast(4)
                val sequence = Sequence(change1, change2, change3, change4)
                if (sequence !in prices) prices[sequence] = price
            }
        }
    }
}

fun part2(lines: List<String>): Int {
    val monkeys = lines.map { Monkey(it.toLong()) }
    val sequences = monkeys.flatMap { it.prices.keys }
    return sequences.maxOf { sequence -> monkeys.sumOf { it.prices.getOrDefault(sequence, 0) } }
}

fun main() {
    val test = false
    val file = File(if (test) "inputs/test.txt" else "inputs/d22.txt")
    val lines = file.readLines()

    // part 1
    println(part1(lines))

    // part 2
    println(part2(lines))
}