package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

sealed class Optional<out T> {
    object None : Optional<Nothing>()
    data class Value<out T>(val value: T) : Optional<T>()
}

val <T> Optional<T>.forcedValue: T get() = (this as Value).value

infix fun <T> Optional<T>.or(default: T): T = when(this) {
    None -> default
    is Value -> value
}

infix fun <T> Optional<T>.or(defaultProvider: () -> T): T = when(this) {
    None -> defaultProvider()
    is Value -> value
}

infix fun <T> Optional<T>.orMaybe(default: Optional<T>): Optional<T> = when(this) {
    None -> default
    is Value -> this
}

infix fun <T> Optional<T>.orMaybe(defaultProvider: () -> Optional<T>): Optional<T> = when(this) {
    None -> defaultProvider()
    is Value -> this
}
