package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value

/**
 * Returns the value associated with the given [key] in the receiver map [this], if any, wrapped in a [Value], or [None]
 * if the receiver map [this] does not contain the given [key].
 *
 * If the type [V] is not nullable, this operation is atomic (meaning it relies on a single call to the receiver map
 * [this]'s [get] function). If the type [V] is nullable, this operation is still atomic in case the map contains the
 * given [key] with a non-`null` value, but in case the map contains the key with a value of `null` or does not contain
 * the given key at all it uses a separate call to the map's [containsKey] function. This might have undesired effects
 * if the map is used concurrently by multiple threads; in particular, when the key is not in the map and is then added
 * to it by another thread, the call might return a [Value] of `null` even if the map never associated the given key
 * with `null`.
 */
inline fun <K, reified V> Map<K, V>.getOrNone(key: K): Optional<V> {
    val ret = get(key)

    // Note: currently (1.7.10), the more concise
    //     return if (ret != null || null is V && containsKey(key)) Value(ret) else None
    // does not compile (KT-53495 or related), nor does
    //     return if (ret != null) Value(ret) else if (ret is V && containsKey(key)) Value(ret) else None
    // even though it's a simple "Replace 'if' expression with return" transformation (KT-53494).
    // A couple of alternatives have other downsides:
    //     return if (ret != null || null is V && containsKey(key)) Value(ret as V) else None
    // does compile, but note the added cast;
    //     return if (ret is V && (ret != null || containsKey(key))) Value(ret) else None
    // does compile, but performs the type check first rather than the null check.
    if (ret != null) return Value(ret)
    return if (ret is V && containsKey(key)) Value(ret) else None
}
