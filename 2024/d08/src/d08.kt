import java.io.File

data class Position(val x: Int, val y: Int) {
    fun isInBounds(size: Int) = x in 0 until size && y in 0 until size
}

data class AntiNodes(val size: Int, val antennas: Map<Char, List<Position>>) {
    val nodes = mutableSetOf<Position>()
    val nodes2 = mutableSetOf<Position>()

    fun findNodes() {
        for ((_, antennas) in antennas) {
            for ((index, antenna1) in antennas.withIndex()) {
                for (antenna2 in antennas.drop(index + 1)) {
                    val dx = antenna2.x - antenna1.x
                    val dy = antenna2.y - antenna1.y

                    val node1 = Position(antenna1.x - dx, antenna1.y - dy)
                    val node2 = Position(antenna2.x + dx, antenna2.y + dy)

                    if (node1.isInBounds(size)) nodes.add(node1)
                    if (node2.isInBounds(size)) nodes.add(node2)
                }
            }
        }
    }

    fun findNodes2() {
        for ((_, antennas) in antennas) {
            for ((index, antenna1) in antennas.withIndex()) {
                for (antenna2 in antennas.drop(index + 1)) {
                    val dx = antenna2.x - antenna1.x
                    val dy = antenna2.y - antenna1.y

                    var node = Position(antenna2.x - dx, antenna2.y - dy)
                    while (node.isInBounds(size)) {
                        nodes2.add(node)
                        node = Position(node.x - dx, node.y - dy)
                    }

                    node = Position(antenna1.x + dx, antenna1.y + dy)
                    while (node.isInBounds(size)) {
                        nodes2.add(node)
                        node = Position(node.x + dx, node.y + dy)
                    }
                }
            }
        }
    }

    fun print() {
        val map = Array(size) { CharArray(size) { '.' } }
        for (node in nodes2) map[node.x][node.y] = '#'
        for ((frequency, antennas) in antennas) for (antenna in antennas) map[antenna.x][antenna.y] = frequency
        for (row in map) println(row.joinToString(""))
    }

    companion object {
        fun parse(lines: List<String>): AntiNodes {
            val size = lines.size
            val antennas = mutableMapOf<Char, List<Position>>()
            for (x in lines.indices) {
                for (y in lines[x].indices) {
                    val frequency = lines[x][y]
                    if (frequency != '.') {
                        antennas.putIfAbsent(frequency, mutableListOf())
                        antennas[frequency] = antennas[frequency]!! + Position(x, y)
                    }
                }
            }
            return AntiNodes(size, antennas)
        }
    }
}

fun main() {
    val file = File("inputs/d08.txt")
    val lines = file.readLines()
    val antiNodes = AntiNodes.parse(lines)

    // part 1
    antiNodes.findNodes()
    println(antiNodes.nodes.size)

    // part 2
    antiNodes.findNodes2()
    println(antiNodes.nodes2.size)
}