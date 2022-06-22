package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

sealed class Optional<out T> {
    object None : Optional<Nothing>()
    data class Value<out T>(val value: T) : Optional<T>()
}

val <T> Optional<T>.forcedValue: T get() = (this as Value).value
fun <T> Optional<T>.forcedValue(exceptionProvider: () -> Throwable): T = or { throw exceptionProvider() }

fun <T, R> Optional<T>.switch(default: R, transform: (T) -> R): R = when (this) {
    None -> default
    is Value -> transform(value)
}
fun <T, R> Optional<T>.switch(defaultProvider: () -> R, transform: (T) -> R): R = when (this) {
    None -> defaultProvider()
    is Value -> transform(value)
}

fun <T> Optional<T>.runOnValue(action: (T) -> Unit) { if (this is Value) action(value) }

infix fun <T> Optional<T>.or(default: T): T = when (this) {
    None -> default
    is Value -> value
}

infix fun <T> Optional<T>.or(defaultProvider: () -> T): T = when (this) {
    None -> defaultProvider()
    is Value -> value
}

infix fun <T> Optional<T>.orMaybe(default: Optional<T>): Optional<T> = when (this) {
    None -> default
    is Value -> this
}

infix fun <T> Optional<T>.orMaybe(defaultProvider: () -> Optional<T>): Optional<T> = when (this) {
    None -> defaultProvider()
    is Value -> this
}

fun <T, R> Optional<T>.map(transform: (T) -> R): Optional<R> = when (this) {
    None -> None
    is Value -> Value(transform(value))
}

fun <T, R> Optional<T>.flatMap(transform: (T) -> Optional<R>): Optional<R> = when (this) {
    None -> None
    is Value -> transform(value)
}

fun <T> Optional<Optional<T>>.flatten(): Optional<T> = when (this) {
    None -> None
    is Value -> value
}
