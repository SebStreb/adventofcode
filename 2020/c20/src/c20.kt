import java.io.File
import kotlin.math.sqrt

fun List<Tile>.corners(): List<Tile> {
    val corners = mutableListOf<Tile>()
    for (tile in this) if (tile.cornerBorders(this).size == 2) corners.add(tile)
    if (corners.size != 4) error("Wrong number of corners: ${corners.size}")
    return corners
}

fun List<Tile>.rightOf(other: Tile) = this.first { it.id != other.id && other.rightOf(it) }
fun List<Tile>.bottomOf(other: Tile) = this.first { it.id != other.id && other.bottomOf(it) }

class Tile(val id: Long, var map: List<List<Char>>) {
    private val top get() = map.first()
    val bottom get() = map.last()
    private val left get() = map.map { it.first() }
    val right get() = map.map { it.last() }

    private val borders get() = listOf(top, bottom, left, right)

    fun cornerBorders(tiles: List<Tile>) = borders.filter { border ->
        tiles.none { other -> id != other.id && other.borders.any { it == border || it.reversed() == border } }
    }

    fun rightOf(other: Tile) = other.borders.any { border -> border == this.right || border.reversed() == this.right }
    fun bottomOf(other: Tile) = other.borders.any { border -> border == this.bottom || border.reversed() == this.bottom }

    fun flipAndRotateToTopLeft(top: List<Char>, left: List<Char>) {
        rotateToTop(top)
        if (this.left != left && this.left != left.reversed()) flipHorizontally()
    }

    fun rotateToTop(top: List<Char>) {
        while (this.top != top && this.top != top.reversed()) rotateClockwise()
        if (this.top == top.reversed()) flipHorizontally()
    }

    fun rotateToLeft(left: List<Char>) {
        while (this.left != left && this.left != left.reversed()) rotateClockwise()
        if (this.left == left.reversed()) flipVertically()
    }

    private fun flipVertically() {
        map = map.reversed()
    }

    private fun flipHorizontally() {
        map = map.map { it.reversed() }
    }

    private fun rotateClockwise() {
        map = List(map.size) { i -> List(map[i].size) { j -> map[map.size - j - 1][i] } }
    }

    override fun toString() = "Tile $id:\n${map.joinToString("\n") { it.joinToString("") }}"

    companion object {
        private fun parseOne(lines: List<String>): Tile {
            val id = lines.first().substring(5, 9).toLong()
            val map = lines.drop(1).map { it.toList() }
            return Tile(id, map)
        }

        fun parse(lines: List<String>): List<Tile> {
            val tiles = mutableListOf<Tile>()
            var remaining = lines
            while (remaining.isNotEmpty()) {
                val tileLines = remaining.takeWhile { it.isNotEmpty() }
                tiles.add(parseOne(tileLines))
                remaining = remaining.dropWhile { it.isNotEmpty() }.drop(1)
            }
            return tiles
        }
    }
}

class Grid(tiles: List<Tile>) {
    val size = sqrt(tiles.size.toDouble()).toInt()
    val tileSize = tiles.first().map.size

    val map = List(size) { MutableList(size) { Tile(0, emptyList()) } }

    init {
        val corners = tiles.corners()
        val topLeft = corners.first()
        val topLeftBorders = topLeft.cornerBorders(tiles)
        topLeft.flipAndRotateToTopLeft(topLeftBorders[0], topLeftBorders[1])
        map[0][0] = topLeft

        for (i in 0..<size) {
            for (j in 0..<size) {
                if (i == 0 && j == 0) continue
                if (j == 0) {
                    val tile = tiles.bottomOf(map[i - 1][j])
                    tile.rotateToTop(map[i - 1][j].bottom)
                    map[i][j] = tile
                } else {
                    val tile = tiles.rightOf(map[i][j - 1])
                    tile.rotateToLeft(map[i][j - 1].right)
                    map[i][j] = tile
                }
            }
        }
    }
}

class Map(grid: Grid) {
    private val tileSize = grid.tileSize - 2
    private var map = List(grid.size * tileSize) { MutableList(grid.size * tileSize) { ' ' } }

    private val monster = listOf(
        "                  # ",
        "#    ##    ##    ###",
        " #  #  #  #  #  #   "
    )

    val monsterSize = monster.sumOf { it.count { c -> c == '#' } }

    init {
        for (i in 0..<grid.size) {
            for (j in 0..<grid.size) {
                val tile = grid.map[i][j]
                for (k in 1..tileSize) {
                    for (l in 1..tileSize) {
                        map[i * tileSize + k - 1][j * tileSize + l - 1] = tile.map[k][l]
                    }
                }
            }
        }
    }

    private fun rotateClockwise() {
        map = List(map.size) { i -> MutableList(map[i].size) { j -> map[map.size - j - 1][i] } }
    }

    private fun flipHorizontally() {
        map = map.map { it.reversed().toMutableList() }
    }

    fun rotateForMonster() {
        for (i in 0..3) {
            if (findMonsters() > 0) return
            rotateClockwise()
        }

        if (findMonsters() > 0) return
        rotateClockwise()
        flipHorizontally()
        rotateForMonster()
    }

    fun findMonsters(): Int {
        var count = 0
        for (i in 0..(map.size - monster.size)) {
            for (j in 0..(map[i].size - monster[0].length)) {
                if (isMonster(i, j)) count++
            }
        }
        return count
    }

    private fun isMonster(i: Int, j: Int): Boolean {
        for (k in monster.indices)
            for (l in monster[k].indices) if (monster[k][l] == '#' && map[i + k][j + l] != '#') return false
        return true
    }

    fun countHashes() = map.sumOf { it.count { c -> c == '#' } }

    override fun toString() = map.joinToString("\n") { it.joinToString("") }
}

fun main() {
    val file = File("inputs/c20")
    val lines = file.readLines()

    val tiles = Tile.parse(lines)
    val corners = tiles.corners()
    println(corners.map { it.id }.reduce { a, b -> a * b })

    val grid = Grid(tiles)
    val map = Map(grid)
    println(map.findMonsters())
    println(map.monsterSize)
    map.rotateForMonster()
    println(map.countHashes() - map.findMonsters() * map.monsterSize)
}