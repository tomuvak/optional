package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

fun <T> Boolean.then(value: T): Optional<T> = if (this) Value(value) else None
fun <T> Boolean.then(valueProvider: () -> T): Optional<T> = if (this) Value(valueProvider()) else None

fun <T> T.ifSatisfies(predicate: (T) -> Boolean): Optional<T> = if (predicate(this)) Value(this) else None
