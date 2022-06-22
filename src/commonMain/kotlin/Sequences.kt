package com.tomuvak.optional

import com.tomuvak.optional.Optional.Value

fun <T> Sequence<Optional<T>>.values(): Sequence<T> = filterIsInstance<Value<T>>().map { it.value }
fun <T> Sequence<Optional<T>>.valuesUntilFirstNone(): Sequence<T> = takeWhile { it is Value }.values()
