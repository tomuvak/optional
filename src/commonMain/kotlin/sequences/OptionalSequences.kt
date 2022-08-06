package com.tomuvak.optional.sequences

import com.tomuvak.optional.Optional
import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

/**
 * Returns a sequence containing the values wrapped in the [Value] elements in the receiver sequence [this], ignoring
 * [None] elements.
 *
 * [valuesUntilFirstNone] is similar, but stops when the first [None] is encountered.
 *
 * This operation is _intermediate_ and _stateless_.
 */
fun <T> Sequence<Optional<T>>.values(): Sequence<T> = filterIsInstance<Value<T>>().map { it.value }

/**
 * Returns a sequence containing the values wrapped in the [Value] elements (if any) of the receiver sequence [this]
 * which appear before the first [None] (or throughout the entire sequence if there is no [None]).
 *
 * Can be used in combination with [generateSequence] with a function which returns the desired sequence elements
 * wrapped in [Value]s and signals the end of the sequence by returning [None].
 *
 * [values] is similar, but returns _all_ values wrapped in [Value]s throughout the entire sequence (not stopping when
 * encountering the first [None]).
 *
 * This operation is _intermediate_ and _stateless_.
 */
fun <T> Sequence<Optional<T>>.valuesUntilFirstNone(): Sequence<T> = takeWhile { it is Value }.values()

/**
 * Returns a list of the values wrapped in the [Value] elements (if any) of the receiver sequence [this] which appear
 * after the last [None] (or throughout the entire sequence if there is no [None]).
 *
 * This operation is _terminal_ (as for a general sequence this cannot be computed without iterating over all of the
 * elements. For this reason it also returns the result as a list rather than as a sequence).
 */
fun <T> Sequence<Optional<T>>.valuesAfterLastNone(): List<T> {
    val buffer = mutableListOf<T>()
    for (element in this) if (element is Value) buffer.add(element.value) else buffer.clear()
    return buffer
}

/**
 * Returns [None] if the receiver sequence [this] contains at least one [None], or a list, wrapped in a [Value], of all
 * values wrapped in the sequence's [Value] elements if the sequence consists solely of [Value] elements.
 *
 * This operation is _terminal_.
 */
fun <T> Sequence<Optional<T>>.valuesIfAll(): Optional<List<T>> {
    val ret = mutableListOf<T>()
    for (element in this) when (element) {
        None -> return None
        is Value -> ret.add(element.value)
    }
    return Value(ret)
}
