import java.io.File
import kotlin.math.abs

data class Pos(val x: Int, val y: Int)
enum class Dir { UP, DOWN, LEFT, RIGHT }
data class Move(val dir: Dir, val amount: Int) {
    companion object {
        fun read(lines: List<String>) = lines.map { line ->
            val data = line.split(" ")
            Move(
                amount = data[1].toInt(), dir = when (data[0]) {
                    "U" -> Dir.UP
                    "D" -> Dir.DOWN
                    "L" -> Dir.LEFT
                    "R" -> Dir.RIGHT
                    else -> error("unknown direction")
                }
            )
        }
    }
}

open class Grid(private val moves: List<Move>) {
    var headPos = Pos(0, 0)
    var tailPos = Pos(0, 0)
    val positions: MutableSet<Pos> = mutableSetOf(tailPos)

    fun doAllMoves() = moves.forEach { doMove(it) }

    private fun needToMove() = abs(headPos.x - tailPos.x) > 1 || abs(headPos.y - tailPos.y) > 1

    open fun updateTail(dir: Dir) {
        when (dir) {
            Dir.UP -> if (needToMove()) tailPos =
                if (tailPos.x == headPos.x) Pos(tailPos.x, tailPos.y + 1)
                else if (tailPos.x < headPos.x) Pos(tailPos.x + 1, tailPos.y + 1)
                else Pos(tailPos.x - 1, tailPos.y + 1)

            Dir.DOWN -> if (needToMove()) tailPos =
                if (tailPos.x == headPos.x) Pos(tailPos.x, tailPos.y - 1)
                else if (tailPos.x < headPos.x) Pos(tailPos.x + 1, tailPos.y - 1)
                else Pos(tailPos.x - 1, tailPos.y - 1)

            Dir.LEFT -> if (needToMove()) tailPos =
                if (tailPos.y == headPos.y) Pos(tailPos.x - 1, tailPos.y)
                else if (tailPos.y < headPos.y) Pos(tailPos.x - 1, tailPos.y + 1)
                else Pos(tailPos.x - 1, tailPos.y - 1)

            Dir.RIGHT -> if (needToMove()) tailPos =
                if (tailPos.y == headPos.y) Pos(tailPos.x + 1, tailPos.y)
                else if (tailPos.y < headPos.y) Pos(tailPos.x + 1, tailPos.y + 1)
                else Pos(tailPos.x + 1, tailPos.y - 1)
        }
        positions.add(tailPos)
    }

    private fun doMove(move: Move) {
        repeat(move.amount) {
            headPos = when (move.dir) {
                Dir.UP -> Pos(headPos.x, headPos.y + 1)
                Dir.DOWN -> Pos(headPos.x, headPos.y - 1)
                Dir.LEFT -> Pos(headPos.x - 1, headPos.y)
                Dir.RIGHT -> Pos(headPos.x + 1, headPos.y)
            }
            updateTail(move.dir)
        }
    }
}

class RecGrid(moves: List<Move>, depth: Int) : Grid(moves) {
    val tail = if (depth < 9) RecGrid(moves, depth + 1) else null

    override fun updateTail(dir: Dir) {
        super.updateTail(dir)
        tail?.headPos = tailPos
        tail?.updateTail(dir)
    }
}

fun main() {
    val file = File("./inputs/c09-test2")
    val lines = file.readLines()
    val grid = RecGrid(Move.read(lines), 0)
    grid.doAllMoves()
    var tail = grid
    while (tail.tail != null) tail = tail.tail!!
    println(tail.positions)
    println(tail.positions.size)
}