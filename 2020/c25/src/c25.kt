import java.io.File


fun transform(subject: Long, loopSize: Int): Long {
    var value = 1L
    repeat(loopSize) {
        value *= subject
        value %= 20201227
    }
    return value
}

fun findLoopSize(subject: Long, publicKey: Long): Int {
    var value = 1L
    var loopSize = 0
    while (value != publicKey) {
        value *= subject
        value %= 20201227
        loopSize++
    }
    return loopSize
}

fun main() {
    val file = File("inputs/c25")
    val lines = file.readLines()

    val cardPublicKey = lines[0].toLong()
    val doorPublicKey = lines[1].toLong()

    val cardLoopSize = findLoopSize(7, cardPublicKey)
    val doorLoopSize = findLoopSize(7, doorPublicKey)

    println(cardLoopSize)
    println(doorLoopSize)
    println(transform(cardPublicKey, doorLoopSize))
    println(transform(doorPublicKey, cardLoopSize))
}