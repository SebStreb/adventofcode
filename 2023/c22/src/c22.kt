import java.io.File

data class Pos2D(val x: Int, val y: Int) {
    constructor(pos3D: Pos3D) : this(pos3D.x, pos3D.y)
}

data class Pos3D(val x: Int, val y: Int, val z: Int) {
    fun copy() = Pos3D(x, y, z)

    companion object {
        fun parse(line: String): Pos3D {
            val parts = line.split(",")
            return Pos3D(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }
    }
}

data class Brick(var positions: Set<Pos3D>) {
    val supportedBy = mutableSetOf<Brick>()

    fun copy() = Brick(positions.map { it.copy() }.toSet())
    fun fall(dz: Int) {
        positions = positions.map { Pos3D(it.x, it.y, it.z - dz) }.toSet()
    }

    companion object {
        private fun parseOne(line: String): Brick {
            val parts = line.split("~")
            val start = Pos3D.parse(parts[0])
            val end = Pos3D.parse(parts[1])

            val positions = mutableSetOf<Pos3D>()
            for (x in start.x..end.x) for (y in start.y..end.y) for (z in start.z..end.z) {
                positions.add(Pos3D(x, y, z))
            }
            return Brick(positions)
        }

        fun parse(lines: List<String>) = lines.map { parseOne(it) }
    }
}

class Sand(private val bricks: List<Brick>) {
    private val maxX = bricks.maxOf { it.positions.maxOf { pos -> pos.x } }
    private val maxY = bricks.maxOf { it.positions.maxOf { pos -> pos.y } }
    private val maxZ = bricks.maxOf { it.positions.maxOf { pos -> pos.z } }

    private val grid = List(maxX + 1) {
        List(maxY + 1) {
            MutableList(maxZ + 1) { 0 }
        }
    }

    init {
        for ((index, brick) in bricks.withIndex()) for (pos in brick.positions) grid[pos.x][pos.y][pos.z] = index + 1
    }

    private fun getFallDistance(brick: Brick): Int {
        var minDistance = Int.MAX_VALUE
        val seen = mutableSetOf<Pos2D>()
        for (pos in brick.positions) {
            if (seen.contains(Pos2D(pos))) continue
            seen.add(Pos2D(pos))

            var distance = 0
            var z = pos.z
            while (z > 1 && grid[pos.x][pos.y][z - 1] == 0) {
                z--
                distance++
            }
            if (minDistance > distance) minDistance = distance
        }
        return minDistance
    }

    fun fall(): Int {
        var count = 0
        for (z in 2..maxZ) {
            for (x in 0..maxX) {
                for (y in 0..maxY) {
                    if (grid[x][y][z] == 0) continue
                    val brickIndex = grid[x][y][z] - 1
                    val brick = bricks[brickIndex]
                    val fallDistance = getFallDistance(brick)
                    if (fallDistance > 0) count++

                    for (pos in brick.positions) {
                        grid[pos.x][pos.y][pos.z] = 0
                        grid[pos.x][pos.y][pos.z - fallDistance] = brickIndex + 1

                        if (pos.z - fallDistance > 1 && grid[pos.x][pos.y][pos.z - fallDistance - 1] != 0) {
                            val supportIndex = grid[pos.x][pos.y][pos.z - fallDistance - 1] - 1
                            if (supportIndex != brickIndex) brick.supportedBy.add(bricks[supportIndex])
                        }
                    }

                    brick.fall(fallDistance)
                }
            }
        }
        return count
    }
}

fun List<Brick>.copy() = this.map { it.copy() }

fun main() {
    val file = File("inputs/c22")
    val lines = file.readLines()

    val bricks = Brick.parse(lines)
    val sand = Sand(bricks)
    sand.fall()

    // part 1
    val wouldCauseAFall = mutableSetOf<Brick>()
    for (brick in bricks) if (brick.supportedBy.size == 1) wouldCauseAFall.add(brick.supportedBy.first())
    println(bricks.size - wouldCauseAFall.size)

    // part 2
    val sum = wouldCauseAFall.sumOf { brick ->
        val s = Sand(bricks.copy() - brick)
        s.fall()
    }
    println(sum)
}