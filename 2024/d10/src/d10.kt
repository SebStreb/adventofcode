import java.io.File

data class Position(val x: Int, val y: Int) {
    private fun isValid(size: Int) = x in 0 until size && y in 0 until size
    fun next(size: Int) = listOf(
        Position(x, y - 1),
        Position(x, y + 1),
        Position(x - 1, y),
        Position(x + 1, y),
    ).filter { it.isValid(size) }
}

data class Trail(val path: MutableList<Position>) {
    val complete get() = path.size == 10
    fun copy() = Trail(path.toMutableList())
}

class TopographicalMap(private val heights: List<List<Int>>) {
    private val size = heights.size

    private val heads = mutableListOf<Position>()
    private val trails = mutableListOf<Trail>()

    fun findTrailHeads() {
        for (y in heights.indices) {
            for (x in heights[y].indices) {
                if (heights[y][x] == 0) {
                    trails.add(Trail(mutableListOf(Position(x, y))))
                    heads.add(Position(x, y))
                }
            }
        }
    }

    private fun score1(head: Position) =
        trails.filter { it.path.first() == head && it.complete }.map { it.path.last() }.toSet().size
    fun totalScore1() = heads.sumOf { score1(it) }

    private fun score2(head: Position) = trails.count { it.path.first() == head && it.complete }
    fun totalScore2() = heads.sumOf { score2(it) }

    fun walkAllTrails() {
        var index = 0
        while (index < trails.size) {
            val trail = trails[index]
            while (!trail.complete) {
                val pos = trail.path.last()
                val height = heights[pos.y][pos.x]

                val next = pos.next(size).filter { heights[it.y][it.x] == height + 1 }
                if (next.isEmpty()) break

                for (n in next.drop(1)) {
                    val nextTrail = trail.copy()
                    nextTrail.path.add(n)
                    trails.add(nextTrail)
                }

                trail.path.add(next.first())
            }
            index++
        }
    }

    companion object {
        fun parse(lines: List<String>) = TopographicalMap(lines.map { line -> line.map { it.digitToInt() } })
    }
}

fun main() {
    val file = File("inputs/d10.txt")
    val lines = file.readLines()

    val map = TopographicalMap.parse(lines)
    map.findTrailHeads()
    map.walkAllTrails()

    // part 1
    println(map.totalScore1())

    // part 2
    println(map.totalScore2())
}