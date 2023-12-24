import java.io.File

data class Option(val holdTime: Long, val travelTime: Long) {
    val distance = holdTime * travelTime
}

data class Race(val totalTime: Long, val record: Long) {
    private val options = (0..totalTime).map { holdTime -> Option(holdTime, travelTime = totalTime - holdTime) }
    val winningOptions = options.filter { it.distance > record }

    companion object {
        fun parse1(lines: List<String>): List<Race> {
            val times = lines.first().drop(5).split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
            val distances = lines.last().drop(9).split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
            return List(times.size) { index -> Race(times[index], distances[index]) }
        }

        fun parse2(lines: List<String>): Race {
            val times = lines.first().drop(5).split(" ").filter { it.isNotEmpty() }
            val distances = lines.last().drop(9).split(" ").filter { it.isNotEmpty() }

            val time = times.reduce { a, b -> a + b }.toLong()
            val distance = distances.reduce { a, b -> a + b }.toLong()
            return Race(time, distance)
        }
    }
}

fun main() {
    val file = File("./inputs/c06")
    val lines = file.readLines()

    // part 1
    val races = Race.parse1(lines)
    println(races.map { it.winningOptions.size }.reduce { a, b -> a * b })

    // part 2
    val race = Race.parse2(lines)
    println(race.winningOptions.size)
}