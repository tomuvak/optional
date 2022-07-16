package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

/**
 * Returns the first element of the receiver sequence [this] wrapped in a [Value], or [None] if the sequence is empty.
 *
 * This operation is _terminal_.
 *
 * This is similar to the standard library's [firstOrNull], but _always_ differentiates between an empty sequence and a
 * non-empty sequence, including when the first element in the sequence has a "special" value (like `null` or [None]).
 */
fun <T> Sequence<T>.firstOrNone(): Optional<T> {
    val iterator = iterator()
    return iterator.hasNext().then(iterator::next)
}

/**
 * Returns the first element of the receiver sequence [this] which satisfies the given [predicate] wrapped in a [Value],
 * or [None] if the sequence contains no element which satisfies the predicate (including when the sequence is empty).
 *
 * This operation is _terminal_.
 *
 * This is similar to the standard library's [firstOrNull], but _always_ differentiates between the case of no element
 * satisfying the [predicate] and the case where the first element satisfying it has a "special" value (like `null` or
 * [None]).
 */
fun <T> Sequence<T>.firstOrNone(predicate: (T) -> Boolean): Optional<T> {
    for (element in this)
        if (predicate(element))
            return Value(element)
    return None
}

/**
 * Returns the last element of the receiver sequence [this] wrapped in a [Value], or [None] if the sequence is empty.
 *
 * This operation is _terminal_.
 *
 * This is similar to the standard library's [lastOrNull], but _always_ differentiates between an empty sequence and a
 * non-empty sequence, including when the last element in the sequence has a "special" value (like `null` or [None]).
 */
fun <T> Sequence<T>.lastOrNone(): Optional<T> {
    val iterator = iterator()
    if (!iterator.hasNext()) return None
    var ret = iterator.next()
    for (element in iterator) ret = element
    return Value(ret)
}

/**
 * Returns the last element of the receiver sequence [this] which satisfies the given [predicate] wrapped in a [Value],
 * or [None] if the sequence contains no element which satisfies the predicate (including when the sequence is empty).
 *
 * This operation is _terminal_.
 *
 * This is similar to the standard library's [lastOrNull], but _always_ differentiates between the case of no element
 * satisfying the [predicate] and the case where the last element satisfying it has a "special" value (like `null` or
 * [None]).
 */
fun <T> Sequence<T>.lastOrNone(predicate: (T) -> Boolean): Optional<T> {
    var ret: T? = null
    var found = false
    for (element in this)
        if (predicate(element)) {
            ret = element
            found = true
        }
    return found.then @Suppress("UNCHECKED_CAST") { ret as T }
}

/**
 * Returns the single element in the receiver sequence [this] wrapped in a [Value], or [None] if the sequence is empty.
 *
 * Throws if the sequence contains more than one element.
 *
 * This operation is _terminal_.
 */
fun <T> Sequence<T>.singleOrNoneIfEmpty(): Optional<T> {
    val iterator = iterator()
    if (!iterator.hasNext()) return None
    val ret = iterator.next()
    return if (iterator.hasNext()) throw IllegalArgumentException("Sequence has more than one element.") else Value(ret)
}

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate] wrapped in a
 * [Value], or [None] if the sequence contains no element satisfying the predicate.
 *
 * Throws if the sequence contains more than one element satisfying the predicate.
 *
 * This operation is _terminal_.
 */
fun <T> Sequence<T>.singleOrNoneIfNone(predicate: (T) -> Boolean): Optional<T> {
    var ret: T? = null
    var found = false
    for (element in this)
        if (predicate(element)) {
            if (found) throw IllegalArgumentException("Sequence contains more than one matching element.")
            found = true
            ret = element
        }
    return found.then @Suppress("UNCHECKED_CAST") { ret as T }
}

/**
 * Returns the single element in the receiver sequence [this] wrapped in a [Value], or [None] if the sequence contains
 * multiple elements.
 *
 * Throws if the sequence is empty.
 *
 * This operation is _terminal_.
 */
fun <T> Sequence<T>.singleOrNoneIfMultiple(): Optional<T> {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException("Sequence is empty.")
    val ret = iterator.next()
    return (!iterator.hasNext()).then(ret)
}

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate] wrapped in a
 * [Value], or [None] if the sequence contains multiple elements satisfying the predicate.
 *
 * Throws if the sequence contains no element satisfying the predicate.
 *
 * This operation is _terminal_.
 */
fun <T> Sequence<T>.singleOrNoneIfMultiple(predicate: (T) -> Boolean): Optional<T> {
    var ret: T? = null
    var found = false
    for (element in this)
        if (predicate(element)) {
            if (found) return None
            found = true
            ret = element
        }
    if (!found) throw NoSuchElementException("Sequence contains no element matching the predicate.")
    @Suppress("UNCHECKED_CAST") return Value(ret as T)
}

/**
 * Returns the single element in the receiver sequence [this] wrapped in a [Value], or [None] if the sequence is empty
 * or contains multiple elements.
 *
 * This operation is _terminal_.
 *
 * This is similar to the standard library's [singleOrNull], but _always_ differentiates between a singleton sequence
 * and a non-singleton sequence, including when the single element in the sequence has a "special" value (like `null` or
 * [None]).
 */
fun <T> Sequence<T>.singleOrNoneIfEmptyOrMultiple(): Optional<T> {
    val iterator = iterator()
    if (!iterator.hasNext()) return None
    val ret = iterator.next()
    return (!iterator.hasNext()).then(ret)
}

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate] wrapped in a
 * [Value], or [None] if the sequence contains either no element satisfying the predicate or multiple such elements.
 *
 * This operation is _terminal_.
 *
 * This is similar to the standard library's [singleOrNull], but _always_ differentiates between the case of a single
 * element satisfying the [predicate] – even if it has a "special" value (like `null` or [None]) – and the case where
 * there are either no or multiple elements satisfying it.
 */
fun <T> Sequence<T>.singleOrNoneIfNoneOrMultiple(predicate: (T) -> Boolean): Optional<T> {
    var ret: T? = null
    var found = false
    for (element in this)
        if (predicate(element)) {
            if (found) return None
            found = true
            ret = element
        }
    return found.then @Suppress("UNCHECKED_CAST") { ret as T }
}

/**
 * Returns the single element in the receiver sequence [this] wrapped in a [Value], or [None] if the sequence is empty.
 * Not supposed to be called on a sequence with more than one element – but this function makes no effort to verify that
 * that's not the case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [firstOrNone], but signals to the reader of calling code that the point is not to take the
 * _first_ element (of potentially multiple elements), but rather the _only_ element (where the fact that there can't be
 * multiple elements has somehow been established prior to the call).
 */
fun <T> Sequence<T>.singleNoVerifyOrNone(): Optional<T> = firstOrNone()

/**
 * Returns the single element in the receiver sequence [this] which satisfies the given [predicate] wrapped in a
 * [Value], or [None] if there is no element in the sequence which satisfies the predicate. Not supposed to be called on
 * a sequence with more than one element satisfying the predicate – but this function makes no effort to verify that
 * that's not the case.
 *
 * This operation is _terminal_.
 *
 * This is equivalent to [firstOrNone], but signals to the reader of calling code that the point is not to take the
 * _first_ element (of potentially multiple elements) which satisfies the predicate, but rather the _only_ element which
 * does (where the fact that there can't be multiple elements satisfying the predicate has somehow been established
 * prior to the call).
 */
fun <T> Sequence<T>.singleNoVerifyOrNone(predicate: (T) -> Boolean): Optional<T> = firstOrNone(predicate)
