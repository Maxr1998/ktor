package io.ktor.utils.io.core

/**
 * Append at most [maxSize] bytes from the specified [other] buffer into this using the end gap reservation if required.
 * @return number of bytes copied
 * @throws IllegalArgumentException if not enough space including end gap
 */
internal fun Buffer.writeBufferAtMost(other: Buffer, maxSize: Int): Int {
    val size = minOf(other.readRemaining, maxSize)

    return write { dst, dstOffset, _ ->
        other.read { src, srcOffset, _ ->
            src.copyTo(dst, srcOffset, size, dstOffset)
            size
        }
    }
}

/**
 * Prepend readable content of the specified [other] buffer to the beginning of this buffer using start gap reservation
 * if required.
 * @return number of bytes copied
 * @throws IllegalArgumentException if not enough space in the beginning to prepend bytes even with start gap
 */
internal fun Buffer.writeBufferPrepend(other: Buffer): Int {
    val size = other.readRemaining
    val readPosition = readPosition

    if (readPosition < size) {
        throw IllegalArgumentException("Not enough space in the beginning to prepend bytes")
    }

    val newReadPosition = readPosition - size
    other.memory.copyTo(memory, other.readPosition, size, newReadPosition)
    other.discardExact(size)
    return size
}
