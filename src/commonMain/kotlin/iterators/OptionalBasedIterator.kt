package com.tomuvak.optional.iterators

import com.tomuvak.optional.Optional
import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.forcedValue
import com.tomuvak.optional.or

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
fun <T> OptionalBasedIterator<T>.toIterator(): Iterator<T> =
    if (this is IteratorBasedOptionalBasedIterator<T>) underlying else OptionalBasedIteratorBasedIterator(this)

private class OptionalBasedIteratorBasedIterator<out T>(
    internal val underlying: OptionalBasedIterator<T>
): Iterator<T> {
    private var cachedNextOrNone: Optional<Optional<T>> = None

    private fun nextOrNone(): Optional<T> =
        cachedNextOrNone or { underlying.nextOrNone().also { cachedNextOrNone = Value(it) } }

    override fun hasNext(): Boolean = nextOrNone() != None
    override fun next(): T = nextOrNone().also { cachedNextOrNone = None }.forcedValue { NoSuchElementException() }
}

/**
 * Returns an [OptionalBasedIterator] which yields the same elements as the receiver iterator [this].
 */
fun <T> Iterator<T>.toOptionalBased(): OptionalBasedIterator<T> =
    if (this is OptionalBasedIteratorBasedIterator<T>) underlying else IteratorBasedOptionalBasedIterator(this)

private class IteratorBasedOptionalBasedIterator<out T>(
    internal val underlying: Iterator<T>
): OptionalBasedIterator<T> { override fun nextOrNone(): Optional<T> = underlying.nextOrNone() }
