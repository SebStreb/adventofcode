import java.io.File
import kotlin.math.sign

data class Point2D(val x: Double, val y: Double)

data class Point3D(val x: Long, val y: Long, val z: Long) : Comparable<Point3D> {
    fun toPoint2D() = Point2D(x.toDouble(), y.toDouble())

    override fun compareTo(other: Point3D) = when {
        x < other.x -> -1
        x > other.x -> 1
        y < other.y -> -1
        y > other.y -> 1
        z < other.z -> -1
        z > other.z -> 1
        else -> 0
    }
}

data class Area(val minX: Long, val maxX: Long, val minY: Long, val maxY: Long)

class LineEquation(start: Point2D, vec: Point2D) {
    // point = start + t * vec (vector form)
    // y = ax + b (general form)

    // (x - start.x) / vec.x = (y - start.y) / vec.y (parametric form)

    // x / vec.x - start.x / vec.x = y / vec.y - start.y / vec.y
    // y / vec.y = x / vec.x + start.y / vec.y - start.x / vec.x
    // y = x * vec.y / vec.x + start.y - start.x * vec.y / vec.x

    // a = vec.y / vec.x
    // b = start.y - start.x * vec.y / vec.x

    private val a = vec.y / vec.x
    private val b = start.y - start.x * vec.y / vec.x
    private fun y(x: Double) = a * x + b

    fun intersection(other: LineEquation): Point2D {
        val x = (other.b - b) / (a - other.a)
        return Point2D(x, y(x))
    }
}

data class HailStone(val start: Point3D, val velocity: Point3D) : Comparable<HailStone> {
    fun toLineEquation() = LineEquation(start.toPoint2D(), velocity.toPoint2D())

    fun isInPast(value: Point2D) = if (velocity.x.sign < 0) value.x > start.x else value.x < start.x

    override fun compareTo(other: HailStone) = start.compareTo(other.start)

    companion object {
        private fun parseOne(line: String): HailStone {
            val (startStr, velocityStr) = line.split(" @ ")
            val (startX, startY, startZ) = startStr.split(", ").map { it.toLong() }
            val (velocityX, velocityY, velocityZ) = velocityStr.split(", ").map { it.toLong() }
            return HailStone(Point3D(startX, startY, startZ), Point3D(velocityX, velocityY, velocityZ))
        }

        fun parse(lines: List<String>) = lines.map { parseOne(it) }
    }
}

fun List<HailStone>.count2DIntersections(area: Area): Int {
    var count = 0
    for (stone1 in this) {
        for (stone2 in this) {
            if (stone1 < stone2) {
                val line1 = stone1.toLineEquation()
                val line2 = stone2.toLineEquation()
                val intersection = line1.intersection(line2)
                if (area.minX <= intersection.x && intersection.x <= area.maxX) {
                    if (area.minY <= intersection.y && intersection.y <= area.maxY) {
                        if (!stone1.isInPast(intersection) && !stone2.isInPast(intersection)) {
                            count++
                        }
                    }
                }
            }
        }
    }
    return count
}

fun main() {
    val test = false
    val file = if (test) File("inputs/c24-test") else File("inputs/c24")
    val lines = file.readLines()

    val testArea = Area(7L, 27L, 7L, 27L)
    val searchArea = Area(2E14.toLong(), 4E14.toLong(), 2E14.toLong(), 4E14.toLong())
    val area = if (test) testArea else searchArea

    val stones = HailStone.parse(lines)
    val count = stones.count2DIntersections(area)
    println(count)
}