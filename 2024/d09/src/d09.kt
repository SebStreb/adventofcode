import kotlin.time.measureTime
import java.io.File as JavaFile

class FileSystem1(private val blocks: MutableList<Int?>) {
    val checksum: Long get() {
        var sum = 0L
        for (i in blocks.indices) {
            if (blocks[i] != null) {
                sum += i.toLong() * blocks[i]!!
            }
        }
        return sum
    }

    fun compact() {
        for (i in blocks.indices) {
            if (blocks[i] == null) {
                val j = blocks.indexOfLast { it != null }
                if (j > i) {
                    blocks[i] = blocks[j]
                    blocks[j] = null
                }
            }
        }
    }

    companion object {
        fun parse(line: String): FileSystem1 {
            var file = true
            var fileId = 0
            val blocks = mutableListOf<Int?>()
            for (char in line) {
                val size = char.digitToInt()
                val id = if (file) fileId++ else null
                repeat(size) { blocks.add(id) }
                file = !file
            }
            return FileSystem1(blocks)
        }
    }
}

abstract class Block(val size: Int)
class Space(size: Int) : Block(size)
class File(val id: Int, size: Int) : Block(size)

class FileSystem2(private val blocks: MutableList<Block>) {
    val checksum: Long get() {
        var sum = 0L
        var blockIndex = 0
        for (block in blocks) {
            for (i in 0 until block.size) {
                if (block is File) sum += blockIndex.toLong() * block.id
                blockIndex++
            }
        }
        return sum
    }

    fun compact() {
        val maxBlockId = blocks.filterIsInstance<File>().maxOf { it.id }
        for (blockId in maxBlockId downTo 0) {
            val j = blocks.indexOfLast { it is File && it.id == blockId }
            val size = blocks[j].size
            for (i in blocks.indices) {
                if (blocks[i] is File && (blocks[i] as File).id == blockId) break
                if (blocks[i] is Space && blocks[i].size >= size) {
                    val remaining = blocks[i].size - size
                    blocks[i] = File(blockId, size)
                    blocks[j] = Space(size)
                    if (remaining > 0) blocks.add(i+1, Space(remaining))
                    break
                }
            }
        }
    }

    companion object {
        fun parse(line: String): FileSystem2 {
            var file = true
            var fileId = 0
            val blocks = mutableListOf<Block>()
            for (char in line) {
                val size = char.digitToInt()
                blocks.add(
                    if (file) File(fileId++, size)
                    else Space(size)
                )
                file = !file
            }
            return FileSystem2(blocks)
        }
    }
}


fun main() {
    val file = JavaFile("inputs/d09.txt")
    val line = file.readLines().first()

    // part 1
    val fs1 = FileSystem1.parse(line)
    measureTime { fs1.compact() }.also { println(it) }
    println(fs1.checksum)

    // part 2
    val fs2 = FileSystem2.parse(line)
    measureTime { fs2.compact() }.also { println(it) }
    println(fs2.checksum)
}