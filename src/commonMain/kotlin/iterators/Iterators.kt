package com.tomuvak.optional.iterators

import com.tomuvak.optional.Optional
import com.tomuvak.optional.then

/**
 * Returns [Optional.None] if the receiver iterator [this] is exhausted, or its next element wrapped in an
 * [Optional.Value] if it's not.
 */
fun <T> Iterator<T>.nextOrNone(): Optional<T> = hasNext().then(::next)
