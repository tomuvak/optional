package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

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

infix fun <T> Optional<T>.or(default: T): T = switch(default) { it }
infix fun <T> Optional<T>.or(defaultProvider: () -> T): T = switch(defaultProvider) { it }
infix fun <T> Optional<T>.orMaybe(default: Optional<T>): Optional<T> = switch(default) { this }
infix fun <T> Optional<T>.orMaybe(defaultProvider: () -> Optional<T>): Optional<T> = switch(defaultProvider) { this }
