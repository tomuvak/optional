package com.tomuvak.optional

import com.tomuvak.optional.Optional.Value

sealed class Optional<out T> {
    object None : Optional<Nothing>()
    data class Value<out T>(val value: T) : Optional<T>()
}

val <T> Optional<T>.forcedValue: T get() = (this as Value).value
