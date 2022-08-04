package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedFunction
import kotlin.test.Test

class MapsTest {
    companion object {
        private const val ExistingKey: String = "ExistingKey"
        private const val ValueForExistingKey: String = "ValueForExistingKey"
        private const val KeyWithValueOfNull: String = "KeyWithValueOfNull"
        private const val NonExistingKey: String = "NonExistingKey"
    }

    private val nonNullableMap = mapOf(ExistingKey to ValueForExistingKey)
    private val nullableMap = mapOf(ExistingKey to ValueForExistingKey, KeyWithValueOfNull to null)

    @Test fun inNonNullableMapGetOrNoneGets() = assertValue(ValueForExistingKey, nonNullableMap.getOrNone(ExistingKey))
    @Test fun inNonNullableMapGetOrNoneOnlyRequiresOneCallToGet() =
        assertValue("value", mockMap<_, String>(mootFunction, scriptedFunction("key" to "value")).getOrNone("key"))
    @Test fun inNonNullableMapGetOrNoneReturnsNone() = assertNone(nonNullableMap.getOrNone(NonExistingKey))
    @Test fun inNonNullableMapGetOrNoneOnlyRequiresOneCallToReturnNone() =
        assertNone(mockMap<_, String>(mootFunction, scriptedFunction("key" to null)).getOrNone("key"))

    @Test fun inNullableMapGetOrNoneGets() = assertValue(ValueForExistingKey, nullableMap.getOrNone(ExistingKey))
    @Test fun inNullableMapGetOrNoneOnlyRequiresOneCallToGetNonNull() =
        assertValue("value", mockMap<_, String?>(mootFunction, scriptedFunction("key" to "value")).getOrNone("key"))
    @Test fun inNullableMapGetOrNoneGetsNull() = assertValue(null, nullableMap.getOrNone(KeyWithValueOfNull))
    @Test fun inNullableMapGetOrNoneReturnsNone() = assertNone(nullableMap.getOrNone(NonExistingKey))

    private fun <K, V> mockMap(containsKey: (K) -> Boolean, get: (K) -> V?): Map<K, V> = object : Map<K, V> {
        override val entries: Set<Map.Entry<K, V>> get() = mootProvider()
        override val keys: Set<K> get() = mootProvider()
        override val size: Int get() = mootProvider()
        override val values: Collection<V> get() = mootProvider()
        override fun isEmpty(): Boolean = mootProvider()
        override fun containsValue(value: V): Boolean = mootFunction(value)

        override fun containsKey(key: K): Boolean = containsKey(key)
        override fun get(key: K): V? = get(key)
    }
}
