package com.tomuvak.optional

fun <T> Optional<T>.orNull(): T? = switch(null) { it }

fun <T : Any> T?.toOptional(): Optional<T> = if (this != null) Optional.Value(this) else Optional.None
