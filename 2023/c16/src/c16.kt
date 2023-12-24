import java.io.File

enum class Direction { UP, DOWN, LEFT, RIGHT }
data class Beam(val x: Int, val y: Int, val facing: Direction)

class Grid(private val grid: List<List<Char>>, firstBeam: Beam) {
    private val beams = mutableListOf(firstBeam)
    private val visited = mutableSetOf(firstBeam)
    val energized = mutableSetOf(Pair(firstBeam.x, firstBeam.y))

    private fun isInGrid(x: Int, y: Int) = x in grid.indices && y in grid[0].indices
    private fun addBeam(x: Int, y: Int, facing: Direction) {
        val beam = Beam(x, y, facing)
        if (isInGrid(x, y) && !visited.contains(beam)) {
            beams.add(beam)
            visited.add(beam)
            energized.add(Pair(x, y))
        }
    }

    private fun execOne() {
        val beam = beams.removeFirst()
        when (grid[beam.x][beam.y]) {
            '.' -> {
                when (beam.facing) {
                    Direction.UP -> addBeam(beam.x - 1, beam.y, Direction.UP)
                    Direction.DOWN -> addBeam(beam.x + 1, beam.y, Direction.DOWN)
                    Direction.LEFT -> addBeam(beam.x, beam.y - 1, Direction.LEFT)
                    Direction.RIGHT -> addBeam(beam.x, beam.y + 1, Direction.RIGHT)
                }
            }

            '/' -> {
                when (beam.facing) {
                    Direction.UP -> addBeam(beam.x, beam.y + 1, Direction.RIGHT)
                    Direction.DOWN -> addBeam(beam.x, beam.y - 1, Direction.LEFT)
                    Direction.LEFT -> addBeam(beam.x + 1, beam.y, Direction.DOWN)
                    Direction.RIGHT -> addBeam(beam.x - 1, beam.y, Direction.UP)
                }
            }

            '\\' -> {
                when (beam.facing) {
                    Direction.UP -> addBeam(beam.x, beam.y - 1, Direction.LEFT)
                    Direction.DOWN -> addBeam(beam.x, beam.y + 1, Direction.RIGHT)
                    Direction.LEFT -> addBeam(beam.x - 1, beam.y, Direction.UP)
                    Direction.RIGHT -> addBeam(beam.x + 1, beam.y, Direction.DOWN)
                }
            }

            '|' -> {
                when (beam.facing) {
                    Direction.UP -> addBeam(beam.x - 1, beam.y, Direction.UP)
                    Direction.DOWN -> addBeam(beam.x + 1, beam.y, Direction.DOWN)
                    Direction.LEFT -> {
                        addBeam(beam.x - 1, beam.y, Direction.UP)
                        addBeam(beam.x + 1, beam.y, Direction.DOWN)
                    }

                    Direction.RIGHT -> {
                        addBeam(beam.x - 1, beam.y, Direction.UP)
                        addBeam(beam.x + 1, beam.y, Direction.DOWN)
                    }
                }
            }

            '-' -> {
                when (beam.facing) {
                    Direction.UP -> {
                        addBeam(beam.x, beam.y - 1, Direction.LEFT)
                        addBeam(beam.x, beam.y + 1, Direction.RIGHT)
                    }

                    Direction.DOWN -> {
                        addBeam(beam.x, beam.y - 1, Direction.LEFT)
                        addBeam(beam.x, beam.y + 1, Direction.RIGHT)
                    }

                    Direction.LEFT -> addBeam(beam.x, beam.y - 1, Direction.LEFT)
                    Direction.RIGHT -> addBeam(beam.x, beam.y + 1, Direction.RIGHT)
                }
            }
        }
    }

    fun execAll() {
        while (beams.isNotEmpty()) execOne()
    }

    companion object {
        fun parse(lines: List<String>) = lines.map { it.toList() }
    }
}

fun main() {
    val file = File("./inputs/c16")
    val lines = file.readLines()
    val map = Grid.parse(lines)

    // part 1
    var grid = Grid(map, Beam(0, 0, Direction.RIGHT))
    grid.execAll()
    println(grid.energized.size)

    // part 2
    var maxEnergized = 0
    for (i in map.indices) {
        grid = Grid(map, Beam(i, 0, Direction.RIGHT))
        grid.execAll()
        maxEnergized = maxOf(maxEnergized, grid.energized.size)

        grid = Grid(map, Beam(i, map[0].size - 1, Direction.LEFT))
        grid.execAll()
        maxEnergized = maxOf(maxEnergized, grid.energized.size)
    }
    for (j in map[0].indices) {
        grid = Grid(map, Beam(0, j, Direction.DOWN))
        grid.execAll()
        maxEnergized = maxOf(maxEnergized, grid.energized.size)

        grid = Grid(map, Beam(map.size - 1, j, Direction.UP))
        grid.execAll()
        maxEnergized = maxOf(maxEnergized, grid.energized.size)
    }
    println(maxEnergized)
}