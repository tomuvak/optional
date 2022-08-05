package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

/**
 * Returns the given [value] wrapped in a [Value] if the receiver boolean [this] is `true`, or [None] if it's `false`.
 *
 * Note that the argument is evaluated in any case – even if the receiver boolean [this] is `false`. To avoid this, use
 * the overload which takes a function.
 */
fun <T> Boolean.then(value: T): Optional<T> = if (this) Value(value) else None

/**
 * If the receiver boolean [this] is `true`, calls [valueProvider] and returns the result wrapped in a [Value].
 * If the receiver boolean [this] is `false`, returns [None] (without calling [valueProvider]).
 *
 * To pass a value directly rather than a function which computes a value, use the other overload.
 */
fun <T> Boolean.then(valueProvider: () -> T): Optional<T> = if (this) Value(valueProvider()) else None

/**
 * Returns the receiver [this] wrapped in a [Value] if it satisfies the given [predicate], or [None] if it doesn't.
 *
 * Equivalent to [Value] ([this]).[filter] ([predicate]).
 */
fun <T> T.ifSatisfies(predicate: (T) -> Boolean): Optional<T> = if (predicate(this)) Value(this) else None

/**
 * Returns the receiver [this] wrapped in a [Value] (of type [Optional]<[T]>) if it is of type [T], or [None] if it's
 * not.
 *
 * This is the [Optional]-based parallel of the built-in "safe" cast operator `as?`. Note that if the receiver [this] is
 * `null`, then the result is a [Value] of `null` if the type [T] is nullable, or [None] if it's not (in other words, if
 * [T] is a nullable type, then, as opposed to the built-in `as?`, this distinguishes between `null` and a non-[T]
 * value).
 */
inline fun <reified T> Any?.ifIs(): Optional<T> = if (this is T) Value(this) else None
