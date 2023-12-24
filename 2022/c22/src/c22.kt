import java.io.File

enum class Tile { EMPTY, OPEN, WALL }
enum class Dir(val value: Int) {
    RIGHT(0), DOWN(1), LEFT(2), UP(3);

    fun turn(dir: Dir): Dir {
        return when (dir) {
            RIGHT -> when (this) {
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
                UP -> RIGHT
            }

            DOWN -> this

            LEFT -> when (this) {
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
                UP -> LEFT
            }

            UP -> this
        }
    }
}

open class Instruction
class Move(val dist: Int) : Instruction()
class Turn(val dir: Dir) : Instruction()

class Puzzle(
    private val rows: Int,
    private val cols: Int,
    private val map: Array<Array<Tile>>,
    private val instructions: MutableList<Instruction>,
) {
    var row = 0
    var col = firstCol()
    var dir = Dir.RIGHT

    fun doAllMoves() {
        while (instructions.isNotEmpty()) executeNext()
    }

    private fun executeNext() {
        when (val instruction = instructions.removeFirst()) {
            is Move -> {
                var dist = instruction.dist
                when (dir) {
                    Dir.RIGHT -> {
                        while (dist > 0) {
                            var newCol = col + 1
                            if (newCol >= cols) newCol = firstOpenColFromStart()
                            if (map[row][newCol] == Tile.EMPTY) newCol = firstOpenColFromStart()
                            if (map[row][newCol] == Tile.WALL) break
                            col = newCol
                            dist--
                        }
                    }

                    Dir.DOWN -> {
                        while (dist > 0) {
                            var newRow = row + 1
                            if (newRow >= rows) newRow = firstOpenRowFromStart()
                            if (map[newRow][col] == Tile.EMPTY) newRow = firstOpenRowFromStart()
                            if (map[newRow][col] == Tile.WALL) break
                            row = newRow
                            dist--
                        }
                    }

                    Dir.LEFT -> {
                        while (dist > 0) {
                            var newCol = col - 1
                            if (newCol <= 0) newCol = firstOpenColFromEnd()
                            if (map[row][newCol] == Tile.EMPTY) newCol = firstOpenColFromEnd()
                            if (map[row][newCol] == Tile.WALL) break
                            col = newCol
                            dist--
                        }
                    }

                    Dir.UP -> {
                        while (dist > 0) {
                            var newRow = row - 1
                            if (newRow <= 0) newRow = firstOpenRowFromEnd()
                            if (map[newRow][col] == Tile.EMPTY) newRow = firstOpenRowFromEnd()
                            if (map[newRow][col] == Tile.WALL) break
                            row = newRow
                            dist--
                        }
                    }
                }
            }

            is Turn -> dir = dir.turn(instruction.dir)
        }
    }

    private fun firstCol(): Int {
        var col = 0
        while (map[row][col] != Tile.OPEN) col++
        return col
    }

    private fun firstOpenColFromStart(): Int {
        var col = 0
        while (map[row][col] == Tile.EMPTY) col++
        return col
    }

    private fun firstOpenColFromEnd(): Int {
        var col = cols - 1
        while (map[row][col] == Tile.EMPTY) col--
        return col
    }

    private fun firstOpenRowFromStart(): Int {
        var row = 0
        while (map[row][col] == Tile.EMPTY) row++
        return row
    }

    private fun firstOpenRowFromEnd(): Int {
        var row = rows - 1
        while (map[row][col] == Tile.EMPTY) row--
        return row
    }

    companion object {
        fun read(lines: List<String>): Puzzle {
            val m = lines.dropLast(2)
            val rows = m.size
            val cols = List(m.size) { i -> m[i].length }.max()

            val map = Array(rows) { i ->
                Array(cols) { j ->
                    if (j >= m[i].length) Tile.EMPTY
                    else when (m[i][j]) {
                        ' ' -> Tile.EMPTY
                        '.' -> Tile.OPEN
                        '#' -> Tile.WALL
                        else -> error("map error")
                    }
                }
            }

            val instructions = mutableListOf<Instruction>()
            var ins = lines.last()
            while (ins.isNotEmpty()) {
                var countDigits = 0
                while (countDigits < ins.length && ins[countDigits].isDigit()) countDigits++
                ins = if (countDigits == 0) {
                    when (ins.first()) {
                        'R' -> instructions.add(Turn(Dir.RIGHT))
                        'L' -> instructions.add(Turn(Dir.LEFT))
                        else -> error("ins error")
                    }
                    ins.drop(1)
                } else {
                    val n = ins.take(countDigits)
                    instructions.add(Move(n.toInt()))
                    ins.drop(countDigits)
                }
            }

            return Puzzle(rows, cols, map, instructions)
        }
    }
}

fun main() {
    val file = File("./inputs/c22-test")
    val lines = file.readLines()
    val puzzle = Puzzle.read(lines)
    puzzle.doAllMoves()
    println("row: ${puzzle.row}, col: ${puzzle.col}, dir: ${puzzle.dir}")
    println((puzzle.row + 1) * 1000 + (puzzle.col + 1) * 4 + puzzle.dir.value)
}