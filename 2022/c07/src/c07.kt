data class FileSystem(val root: Dir = Dir("/")) {
    companion object {
        const val MAX_SIZE = 70000000
        const val SPACE_NEEDED = 30000000
    }
    private val totalSize: Int get() = root.size
    private val unused: Int get() = MAX_SIZE - totalSize
    val missing: Int get() = SPACE_NEEDED - unused
}

abstract class FSNode(val name: String, val parent: Dir? = null) {
    abstract val size: Int
    abstract fun printFS(indent: Int): String
}

class Dir(name: String, parent: Dir? = null, var contents: MutableSet<FSNode> = mutableSetOf()) : FSNode(name, parent) {
    override val size get() = contents.sumOf { it.size }
    override fun toString() = "- dir $name ($size)"
    override fun printFS(indent: Int) =
        " ".repeat(indent) + this + "\n" + contents.joinToString("\n") { it.printFS(indent + 4) }
}

class File(name: String, override val size: Int, parent: Dir) : FSNode(name, parent) {
    override fun toString() = "- $name ($size)"
    override fun printFS(indent: Int) = " ".repeat(indent) + this
}

fun nextLine(lines: Iterator<String>) = if (lines.hasNext()) lines.next() else "$ exit"
fun constructFS(fs: FileSystem, lines: Iterator<String>) {
    var currentDirectory = fs.root
    var line = nextLine(lines)
    while (line != "$ exit") {

        when (line) {
            "$ ls" -> {
                if (currentDirectory.contents.isEmpty()) {
                    line = nextLine(lines)
                    while (!line.startsWith("$ ")) {
                        val data = line.split(" ")
                        currentDirectory.contents +=
                            if (data[0] == "dir") Dir(data[1], currentDirectory)
                            else File(data[1], data[0].toInt(), currentDirectory)
                        line = nextLine(lines)
                    }
                }
            }

            "$ cd /" -> {
                currentDirectory = fs.root
                line = nextLine(lines)
            }

            "$ cd .." -> {
                currentDirectory = currentDirectory.parent ?: error("cd .. on root")
                line = nextLine(lines)
            }

            else -> {
                val dest = line.removePrefix("$ cd ")
                val dir = currentDirectory.contents.first { it.name == dest } as Dir
                currentDirectory = dir
                line = nextLine(lines)
            }
        }

    }
}

fun getSmallDirs(dir: Dir): List<Dir> {
    val lst = mutableListOf<Dir>()
    if (dir.size <= 100000) lst.add(dir)
    dir.contents.filterIsInstance<Dir>().forEach { subDir -> lst.addAll(getSmallDirs(subDir)) }
    return lst
}

fun optimizeDir(missing: Int, currentDir: Dir): Dir? {
    if (currentDir.size < missing) return null
    val subDirs = currentDir.contents.filterIsInstance<Dir>()
    if (subDirs.isEmpty()) return currentDir
    val optimizeSubDirs = subDirs.mapNotNull { optimizeDir(missing, it) }
    if (optimizeSubDirs.isEmpty()) return currentDir
    return optimizeSubDirs.minBy { it.size }
}

fun main() {
    val fs = FileSystem()
    val file = java.io.File("./inputs/c07")
    val lines = file.readLines().iterator()
    constructFS(fs, lines)

    println(optimizeDir(fs.missing, fs.root))

}