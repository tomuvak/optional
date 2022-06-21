package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

fun <T> Sequence<Optional<T>>.values(): Sequence<T> = filterIsInstance<Value<T>>().map { it.value }
fun <T> Sequence<Optional<T>>.valuesUntilFirstNone(): Sequence<T> = sequence {
    for (optional in this@valuesUntilFirstNone) when (optional) {
        None -> return@sequence
        is Value -> yield(optional.value)
    }
}
