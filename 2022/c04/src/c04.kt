import java.io.File

data class Assignment(val start: Int, val end: Int) {
    fun isIncludedIn(that: Assignment) = that.start <= this.start && this.end <= that.end
    fun overlaps(that: Assignment): Boolean {
        val rangeA = IntRange(this.start, this.end)
        val rangeB = IntRange(that.start, that.end)
        return rangeA.any { rangeB.contains(it) }
    }
}

fun getAssignment(descriptor: String): Assignment {
    val data = descriptor.split("-")
    return Assignment(data[0].toInt(), data[1].toInt())
}

fun getAssignmentPair(line: String): Pair<Assignment, Assignment> {
    val data = line.split(",")
    return Pair(getAssignment(data[0]), getAssignment(data[1]))
}

fun getAssignmentPairs(lines: List<String>) = lines.map { getAssignmentPair(it) }

fun main() {
    val file = File("./inputs/c04")
    val lines = file.readLines()
    val assignmentPairs = getAssignmentPairs(lines)
    val count = assignmentPairs.count { (a, b) -> a.overlaps(b) }
    println(count)
}