import java.io.File

fun part1(lines: List<String>): Int {
    val maxRed = 12
    val maxGreen = 13
    val maxBlue = 14

    var sum = 0

    for (line in lines) {
        val game = line.split(": ")
        val gameId = game[0].drop(5).toInt()

        var possible = true

        val sets = game[1].split("; ")
        for (set in sets) {
            val cubes = set.split(", ")
            for (cube in cubes) {
                val data = cube.split(" ")
                val cubeCount = data[0].toInt()
                val cubeColor = data[1]

                when (cubeColor) {
                    "red" -> if (cubeCount > maxRed) possible = false
                    "green" -> if (cubeCount > maxGreen) possible = false
                    "blue" -> if (cubeCount > maxBlue) possible = false
                }
            }
        }

        if (possible) sum += gameId
    }

    return sum
}

fun part2(lines: List<String>): Int {
    var sum = 0

    for (line in lines) {
        var maxRed = 0
        var maxGreen = 0
        var maxBlue = 0

        val sets = line.split(": ")[1].split("; ")
        for (set in sets) {
            val cubes = set.split(", ")
            for (cube in cubes) {
                val data = cube.split(" ")
                val cubeCount = data[0].toInt()
                val cubeColor = data[1]

                when (cubeColor) {
                    "red" -> if (cubeCount > maxRed) maxRed = cubeCount
                    "green" -> if (cubeCount > maxGreen) maxGreen = cubeCount
                    "blue" -> if (cubeCount > maxBlue) maxBlue = cubeCount
                }
            }
        }

        val power = maxRed * maxGreen * maxBlue
        sum += power
    }

    return sum
}

fun main() {
    val file = File("./inputs/c02")
    val lines = file.readLines()

    println(part1(lines))
    println(part2(lines))
}