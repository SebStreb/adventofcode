import java.io.File
import java.util.PriorityQueue

data class Pos(val x: Int, val y: Int)
data class State(val pos: Pos, val direction: String, val straightLine: Int) {

    fun next1(maxX: Int, maxY: Int): List<State> {
        val x = pos.x
        val y = pos.y

        val straightLineUp = State(Pos(x - 1, y), "up", straightLine + 1)
        val straightLineDown = State(Pos(x + 1, y), "down", straightLine + 1)
        val straightLineLeft = State(Pos(x, y - 1), "left", straightLine + 1)
        val straightLineRight = State(Pos(x, y + 1), "right", straightLine + 1)

        val up = State(Pos(x - 1, y), "up", 1)
        val down = State(Pos(x + 1, y), "down", 1)
        val left = State(Pos(x, y - 1), "left", 1)
        val right = State(Pos(x, y + 1), "right", 1)

        val next = mutableListOf<State>()
        when (direction) {
            "up" -> {
                if (straightLine <= 2 && x - 1 >= 0) next.add(straightLineUp)
                if (y - 1 >= 0) next.add(left)
                if (y + 1 <= maxY) next.add(right)
            }

            "down" -> {
                if (straightLine <= 2 && x + 1 <= maxX) next.add(straightLineDown)
                if (y - 1 >= 0) next.add(left)
                if (y + 1 <= maxY) next.add(right)
            }

            "left" -> {
                if (straightLine <= 2 && y - 1 >= 0) next.add(straightLineLeft)
                if (x - 1 >= 0) next.add(up)
                if (x + 1 <= maxX) next.add(down)
            }

            "right" -> {
                if (straightLine <= 2 && y + 1 <= maxY) next.add(straightLineRight)
                if (x - 1 >= 0) next.add(up)
                if (x + 1 <= maxX) next.add(down)
            }

            "none" -> {
                if (x - 1 >= 0) next.add(up)
                if (x + 1 <= maxX) next.add(down)
                if (y - 1 >= 0) next.add(left)
                if (y + 1 <= maxY) next.add(right)
            }
        }
        return next
    }

    fun next2(maxX: Int, maxY: Int): List<State> {
        val x = pos.x
        val y = pos.y

        val straightLineUp = State(Pos(x - 1, y), "up", straightLine + 1)
        val straightLineDown = State(Pos(x + 1, y), "down", straightLine + 1)
        val straightLineLeft = State(Pos(x, y - 1), "left", straightLine + 1)
        val straightLineRight = State(Pos(x, y + 1), "right", straightLine + 1)

        val up = State(Pos(x - 1, y), "up", 1)
        val down = State(Pos(x + 1, y), "down", 1)
        val left = State(Pos(x, y - 1), "left", 1)
        val right = State(Pos(x, y + 1), "right", 1)

        val next = mutableListOf<State>()
        when (direction) {
            "up" -> {
                if (straightLine < 4) {
                    if (x - 1 >= 0) next.add(straightLineUp)
                } else if (straightLine < 10) {
                    if (x - 1 >= 0) next.add(straightLineUp)
                    if (y - 1 >= 0) next.add(left)
                    if (y + 1 <= maxY) next.add(right)
                } else {
                    if (y - 1 >= 0) next.add(left)
                    if (y + 1 <= maxY) next.add(right)
                }
            }

            "down" -> {
                if (straightLine < 4) {
                    if (x + 1 <= maxX) next.add(straightLineDown)
                } else if (straightLine < 10) {
                    if (x + 1 <= maxX) next.add(straightLineDown)
                    if (y - 1 >= 0) next.add(left)
                    if (y + 1 <= maxY) next.add(right)
                } else {
                    if (y - 1 >= 0) next.add(left)
                    if (y + 1 <= maxY) next.add(right)
                }
            }

            "left" -> {
                if (straightLine < 4) {
                    if (y - 1 >= 0) next.add(straightLineLeft)
                } else if (straightLine < 10) {
                    if (y - 1 >= 0) next.add(straightLineLeft)
                    if (x - 1 >= 0) next.add(up)
                    if (x + 1 <= maxX) next.add(down)
                } else {
                    if (x - 1 >= 0) next.add(up)
                    if (x + 1 <= maxX) next.add(down)
                }
            }

            "right" -> {
                if (straightLine < 4) {
                    if (y + 1 <= maxY) next.add(straightLineRight)
                } else if (straightLine < 10) {
                    if (y + 1 <= maxY) next.add(straightLineRight)
                    if (x - 1 >= 0) next.add(up)
                    if (x + 1 <= maxX) next.add(down)
                } else {
                    if (x - 1 >= 0) next.add(up)
                    if (x + 1 <= maxX) next.add(down)
                }
            }

            "none" -> {
                if (x - 1 >= 0) next.add(up)
                if (x + 1 <= maxX) next.add(down)
                if (y - 1 >= 0) next.add(left)
                if (y + 1 <= maxY) next.add(right)
            }
        }
        return next
    }
}

data class Path(val state: State, val accLoss: Int) : Comparable<Path> {
    fun next1(maxX: Int, maxY: Int, losses: List<List<Int>>) =
        state.next1(maxX, maxY).map { Path(it, accLoss + losses[it.pos.x][it.pos.y]) }

    fun next2(maxX: Int, maxY: Int, losses: List<List<Int>>) =
        state.next2(maxX, maxY).map { Path(it, accLoss + losses[it.pos.x][it.pos.y]) }

    override fun compareTo(other: Path) = accLoss.compareTo(other.accLoss)
}

fun dijkstra(
    start: Pos, end: Pos,
    maxX: Int, maxY: Int,
    losses: List<List<Int>>,
    part2: Boolean = false,
): Path? {
    val priorityQueue = PriorityQueue<Path>()
    val visited = mutableSetOf<State>()

    val state = State(start, "none", 1)
    priorityQueue.add(Path(state, 0))

    while (priorityQueue.isNotEmpty()) {
        val currentPath = priorityQueue.poll()

        if (currentPath.state.pos == end) return currentPath // Found the optimal path

        if (visited.contains(currentPath.state)) continue
        visited.add(currentPath.state)

        val nextPaths = if (part2) currentPath.next2(maxX, maxY, losses) else currentPath.next1(maxX, maxY, losses)
        priorityQueue.addAll(nextPaths)
    }

    return null // No path found
}

fun main() {
    val file = File("./inputs/c17")
    val lines = file.readLines()

    val losses = lines.map { line -> line.map { it.digitToInt() } }

    val maxX = losses.size - 1
    val maxY = losses[0].size - 1

    val start = Pos(0, 0)
    val dest = Pos(maxX, maxY)

    val path = dijkstra(start, dest, maxX, maxY, losses)!!
    println(path.accLoss)

    val path2 = dijkstra(start, dest, maxX, maxY, losses, part2 = true)!!
    println(path2.accLoss)
}