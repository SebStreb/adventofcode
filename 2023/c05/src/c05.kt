import java.io.File
import kotlin.time.measureTime

class Transform(sourceStart: Long, destinationStart: Long, length: Long) {
    val range = LongRange(sourceStart, sourceStart + length - 1)
    private val delta = destinationStart - sourceStart
    fun convert(num: Long) = num + delta
}

class Step(private val transforms: List<Transform>) {
    fun convert(num: Long) = transforms.firstOrNull { it.range.contains(num) }?.convert(num) ?: num
}

abstract class Almanac(private val steps: List<Step>) {
    fun getLocation(seed: Long): Long {
        var value = seed
        for (step in steps) value = step.convert(value)
        return value
    }

    companion object {
        fun parseSteps(lines: List<String>): List<Step> {
            val steps = mutableListOf<Step>()

            var remaining = lines
            repeat(7) {
                val transforms = mutableListOf<Transform>()
                while (remaining.isNotEmpty() && remaining.first().isNotEmpty()) {
                    val data = remaining.first().split(" ")
                    transforms += Transform(data[1].toLong(), data[0].toLong(), data[2].toLong())
                    remaining = remaining.drop(1)
                }
                steps += Step(transforms)
                if (remaining.isNotEmpty()) remaining = remaining.drop(2)
            }

            return steps
        }
    }
}

class ListAlmanac(seeds: List<Long>, steps: List<Step>) : Almanac(steps) {

    val locations = seeds.map { seed -> getLocation(seed) }

    companion object {
        fun parse(lines: List<String>): ListAlmanac {
            val seeds = lines.first().drop(7).split(" ").map { it.toLong() }
            val steps = parseSteps(lines.drop(3))
            return ListAlmanac(seeds, steps)
        }
    }
}

class RangeAlmanac(private val ranges: List<LongRange>, steps: List<Step>) : Almanac(steps) {

    val minLocation: Long get() {
        var min = Long.MAX_VALUE

        for (range in ranges) {
            for (seed in range) {
                val location = getLocation(seed)
                if (min > location) min = location
            }
        }

        return min
    }

    companion object {
        fun parse(lines: List<String>): RangeAlmanac {
            var nums = lines.first().drop(7).split(" ").map { it.toLong() }
            val ranges = mutableListOf<LongRange>()

            while (nums.isNotEmpty()) {
                ranges += LongRange(nums[0], nums[0] + nums[1] - 1)
                nums = nums.drop(2)
            }

            val steps = parseSteps(lines.drop(3))
            return RangeAlmanac(ranges, steps)
        }
    }
}

fun main() {
    val file = File("./inputs/c05")
    val lines = file.readLines()

    // part 1
    val listAlmanac = ListAlmanac.parse(lines)
    println(listAlmanac.locations.min())

    // part 2
    val time = measureTime {
        val rangeAlmanac = RangeAlmanac.parse(lines)
        println(rangeAlmanac.minLocation)
    }
    println(time)
}