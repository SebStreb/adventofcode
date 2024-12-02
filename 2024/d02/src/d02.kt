import java.io.File
import kotlin.math.abs

data class Report(val levels: List<Int>) {

    val isSafe1: Boolean get() {
        var previous = 0
        var current = 1
        val increasing = levels[current] > levels[previous]

        while (current < levels.size) {
            if (!safe(levels[previous], levels[current], increasing)) return false
            previous = current
            current += 1
        }

        return true
    }

    val isSafe2: Boolean get() {
        if (isSafe1) return true
        for (i in levels.indices) {
            val report = Report(levels.toMutableList().apply { removeAt(i) })
            if (report.isSafe1) return true
        }
        return false
    }

    companion object {
        fun safe(previous: Int, current: Int, increasing: Boolean): Boolean {
            if (previous == current) return false
            if (increasing && previous > current) return false
            if (!increasing && previous < current) return false
            val diff = abs(previous - current)
            return diff <= 3
        }

        fun getLines(fileName: String): List<String> {
            val inputFile = File(fileName)
            return inputFile.readLines()
        }

        fun getReports(lines: List<String>): List<Report> {
            val reports = mutableListOf<Report>()
            for (line in lines) {
                val levels = line.split(" ").map { it.toInt() }
                reports.add(Report(levels))
            }
            return reports
        }
    }
}

fun main() {
    val lines = Report.getLines("inputs/d02.txt")
    val reports = Report.getReports(lines)

    println(reports.count { it.isSafe1 })
    println(reports.count { it.isSafe2 })
}