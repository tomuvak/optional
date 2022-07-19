package com.tomuvak.optional.sequences

import com.tomuvak.optional.Optional
import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

fun <T> Sequence<Optional<T>>.values(): Sequence<T> = filterIsInstance<Value<T>>().map { it.value }
fun <T> Sequence<Optional<T>>.valuesUntilFirstNone(): Sequence<T> = takeWhile { it is Value }.values()
fun <T> Sequence<Optional<T>>.valuesIfAll(): Optional<List<T>> {
    val ret = mutableListOf<T>()
    for (element in this) when (element) {
        None -> return None
        is Value -> ret.add(element.value)
    }
    return Value(ret)
}
