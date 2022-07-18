package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

/**
 * An alternative to the standard library's [Iterator] interface, based on a single [nextOrNone] function rather than
 * two separate functions for querying whether there is a next element (`hasNext`) and for actually retrieving the next
 * element (`next`).
 *
 * Can be converted to an [Iterator] using the [toIterator] extension function, or produced from an [Iterator] using the
 * extension function [toOptionalBased].
 *
 * Among other use cases, implementing this interface and converting it to an [Iterator] may serve as a more functional
 * alternative to subclassing the standard library's [AbstractIterator].
 */
fun interface OptionalBasedIterator<out T> { fun nextOrNone(): Optional<T> }

/**
 * Returns an [Iterator] which yields the same elements as the receiver [Optional]-based iterator [this].
 */
fun <T> OptionalBasedIterator<T>.toIterator(): Iterator<T> = OptionalBasedIteratorBasedIterator(this)

private class OptionalBasedIteratorBasedIterator<out T>(private val underlying: OptionalBasedIterator<T>): Iterator<T> {
    private var cachedNextOrNone: Optional<Optional<T>> = None

    private fun nextOrNone(): Optional<T> =
        cachedNextOrNone or { underlying.nextOrNone().also { cachedNextOrNone = Value(it) } }

    override fun hasNext(): Boolean = nextOrNone() != None
    override fun next(): T = nextOrNone().also { cachedNextOrNone = None }.forcedValue { NoSuchElementException() }
}
