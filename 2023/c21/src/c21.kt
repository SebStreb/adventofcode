import java.io.File
import kotlin.math.pow

data class Pos(val x: Int, val y: Int) {
    fun next(map: List<List<Char>>): List<Pos> {
        val res = mutableListOf<Pos>()
        if (x > 0 && map[x - 1][y] == '.') res.add(Pos(x - 1, y))
        if (x < map.size - 1 && map[x + 1][y] == '.') res.add(Pos(x + 1, y))
        if (y > 0 && map[x][y - 1] == '.') res.add(Pos(x, y - 1))
        if (y < map[0].size - 1 && map[x][y + 1] == '.') res.add(Pos(x, y + 1))
        return res
    }
}
class Garden1(private val map: List<List<Char>>, startPos: Pos) {
    private var steps = 0
    var positions = setOf(startPos)

    private fun step() {
        steps++
        positions = positions.flatMap { it.next(map) }.toSet()
    }

    fun stepUntil(steps: Int) {
        while (this.steps < steps) step()
    }

    companion object {
        fun parse(lines: List<String>): Garden1 {
            var map = lines.map { it.toList() }
            var startPos = Pos(0, 0)

            map = List(map.size) { i ->
                List(map[0].size) { j ->
                    when (map[i][j]) {
                        'S' -> {
                            startPos = Pos(i, j)
                            '.'
                        }
                        else -> map[i][j]
                    }
                }
            }

            return Garden1(map, startPos)
        }
    }
}

data class InfiPos(val x: Int, val y: Int, val mapPos: Set<Pos>) {
    fun next(map: List<List<Char>>): List<InfiPos> {
        val res = mutableListOf<InfiPos>()

        if (x - 1 < 0) res.add(InfiPos(map.size - 1, y, mapPos.map { Pos(it.x - 1, it.y) }.toSet()))
        else if (map[x - 1][y] == '.') res.add(InfiPos(x - 1, y, mapPos))

        if (x + 1 >= map.size) res.add(InfiPos(0, y, mapPos.map { Pos(it.x + 1, it.y) }.toSet()))
        else if (map[x + 1][y] == '.') res.add(InfiPos(x + 1, y, mapPos))

        if (y - 1 < 0) res.add(InfiPos(x, map[0].size - 1, mapPos.map { Pos(it.x, it.y - 1) }.toSet()))
        else if (map[x][y - 1] == '.') res.add(InfiPos(x, y - 1, mapPos))

        if (y + 1 >= map[0].size) res.add(InfiPos(x, 0, mapPos.map { Pos(it.x, it.y + 1) }.toSet()))
        else if (map[x][y + 1] == '.') res.add(InfiPos(x, y + 1, mapPos))

        return res
    }
}

class Garden2(private val map: List<List<Char>>, startPos: InfiPos) {
    private var steps = 0
    var positions = listOf(startPos)

    private fun step() {
        steps++

        val newPositions = mutableListOf<InfiPos>()
        for (pos in positions) {
            val next = pos.next(map)
            for (infiPos in next) {
                val index = newPositions.indexOfFirst { it.x == infiPos.x && it.y == infiPos.y }
                if (index != -1) {
                    newPositions[index] = InfiPos(infiPos.x, infiPos.y, newPositions[index].mapPos + infiPos.mapPos)
                } else newPositions.add(infiPos)
            }
        }
        positions = newPositions
    }

    fun stepUntil(steps: Int) {
        while (this.steps < steps) step()
    }

    companion object {
        fun parse(lines: List<String>): Garden2 {
            var map = lines.map { it.toList() }
            var startPos = InfiPos(0, 0, setOf(Pos(0, 0)))

            map = List(map.size) { i ->
                List(map[0].size) { j ->
                    when (map[i][j]) {
                        'S' -> {
                            startPos = InfiPos(i, j, setOf(Pos(0, 0)))
                            '.'
                        }
                        else -> map[i][j]
                    }
                }
            }

            return Garden2(map, startPos)
        }
    }
}

fun main() {
    val file = File("inputs/c21")
    val lines = file.readLines()

    // part 1
    val garden1 = Garden1.parse(lines)
    garden1.stepUntil(64)
    println(garden1.positions.size)

    // part 2
    val x = 26501365

    val a = lines.size
    val b = lines.size / 2
    val c = (x - b) / a

    println("$a, $b, $c, $x")

    var garden2 = Garden2.parse(lines)
    garden2.stepUntil(b)
    val fN1 = garden2.positions.sumOf { it.mapPos.size  }
    println("f($b) = $fN1")

    garden2 = Garden2.parse(lines)
    garden2.stepUntil(b + a)
    val fN2 = garden2.positions.sumOf { it.mapPos.size  }
    println("f(${b + a}) = $fN2")

    garden2 = Garden2.parse(lines)
    garden2.stepUntil(b + 2 * a)
    val fN3 = garden2.positions.sumOf { it.mapPos.size  }
    println("f(${b + 2 * a}) = $fN3")

    // found using wolfram alpha
    fun f(x: Double) = 14494 * x.pow(2.0) / 17161 + 38467 * x / 17161 + 187220 / 17161
    println("f($b) = ${f(b.toDouble())}")
    println("f(${b + a}) = ${f((b + a).toDouble())}")
    println("f(${b + 2 * a}) = ${f((b + 2 * a).toDouble())}")
    println("f($x) = ${f(x.toDouble())}")
    println(f(x.toDouble()).toLong() + 1)
}