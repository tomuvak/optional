package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

fun <T> Optional<T>.orNull(): T? = switch(null) { it }

fun <T : Any> T?.toOptional(): Optional<T> = this?.let(::Value) ?: None
