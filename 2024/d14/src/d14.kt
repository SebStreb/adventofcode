import java.io.File

data class Position(val x: Int, val y: Int)
data class Velocity(val x: Int, val y: Int)
data class Robot(val position: Position, val velocity: Velocity) {
    companion object {
        fun parse(line: String): Robot {
            val (posPart, velPart) = line.split(" ")
            val (xPart, yPart) = posPart.split(",")
            val (vxPart, vyPart) = velPart.split(",")

            val x = xPart.drop(2).toInt()
            val y = yPart.toInt()
            val vx = vxPart.drop(2).toInt()
            val vy = vyPart.toInt()

            return Robot(Position(x, y), Velocity(vx, vy))
        }
    }
}

class Bathroom(private var robots: List<Robot>) {
    private val width = 101
    private val height = 103

    private fun numberOfRobots(minX: Int, minY: Int, maxX: Int, maxY: Int) = robots.count {
        it.position.x in minX..maxX && it.position.y in minY..maxY
    }

    val safetyFactor: Int get() {
        val midX = width / 2
        val midY = height / 2
        return numberOfRobots(0, 0, midX - 1, midY - 1) *
                numberOfRobots(midX + 1, 0, width - 1, midY - 1) *
                numberOfRobots(0, midY + 1, midX - 1, height - 1) *
                numberOfRobots(midX + 1, midY + 1, width - 1, height - 1)
    }

    private fun nextSecond() {
        robots = robots.map { robot ->
            var x = robot.position.x + robot.velocity.x
            var y = robot.position.y + robot.velocity.y
            if (x < 0) x += width
            if (y < 0) y += height
            if (x >= width) x -= width
            if (y >= height) y -= height
            Robot(Position(x, y), robot.velocity)
        }
    }

    fun after(seconds: Int) = repeat(seconds) { nextSecond() }

    private fun isEasterEgg() = toString().contains("111111111111111111111111111111")

    fun untilEasterEgg(): Int {
        var seconds = 0
        while (!isEasterEgg()) {
            nextSecond()
            seconds++
        }
        println(this)
        return seconds
    }

    override fun toString(): String {
        var str = ""
        for (y in 0 until height) {
            for (x in 0 until width) {
                val count = robots.count { it.position.x == x && it.position.y == y }
                str += if (count == 0) '.' else count
            }
            str += "\n"
        }
        return str
    }

    companion object {
        fun parse(lines: List<String>) = Bathroom(lines.map { Robot.parse(it) })
    }
}

fun main() {
    val file = File("inputs/d14.txt")
    val lines = file.readLines()

    // part 1
    val bathroom1 = Bathroom.parse(lines)
    bathroom1.after(100)
    println(bathroom1.safetyFactor)
    println()

    // part 2
    val bathroom2 = Bathroom.parse(lines)
    println(bathroom2.untilEasterEgg())
}