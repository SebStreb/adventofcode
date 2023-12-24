import java.io.File

fun findMarker(stream: String): Int {
    var i = 14
    while (i < stream.length) {
        if (listOf(
                stream[i-13], stream[i-12], stream[i-11], stream[i-10],
                stream[i-9], stream[i-8], stream[i-7], stream[i-6],
                stream[i-5], stream[i-4], stream[i-3], stream[i-2],
                stream[i-1], stream[i]
            ).distinct().size == 14)
            return i+1
        i++
    }
    return 0
}

fun main() {
    val file = File("./inputs/c06")
    val line = file.readLines().first()
    val result = findMarker(line)
    println(result)
}