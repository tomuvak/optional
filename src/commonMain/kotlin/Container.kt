package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> = switch(None) { Value(transform(it)) }
fun <T, R> Optional<T>.flatMap(transform: (T) -> Optional<R>): Optional<R> = switch(None, transform)
fun <T> Optional<Optional<T>>.flatten(): Optional<T> = switch(None) { it }

fun <T> Optional<T>.filter(predicate: (T) -> Boolean): Optional<T> = switch(None) { if (predicate(it)) this else None }
fun <T> Optional<T>.filterNot(predicate: (T) -> Boolean): Optional<T> =
    switch(None) { if (predicate(it)) None else this }
inline fun <reified T> Optional<Any?>.filterIsInstance(): Optional<T> =
    switch(None) { if (it is T) Value(it) else None }

fun <T> Optional<T>.asSequence(): Sequence<T> = switch(::emptySequence) { sequenceOf(it) }
fun <T> Optional<T>.asIterable(): Iterable<T> = asSequence().asIterable()
fun <T> Optional<T>.toList(): List<T> = switch(::emptyList) { listOf(it) }
inline fun <reified T> Optional<T>.toTypedArray(): Array<T> = toList().toTypedArray()
