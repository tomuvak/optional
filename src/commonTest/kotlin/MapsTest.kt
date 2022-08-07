package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedFunction
import kotlin.test.Test

class MapsTest {
    companion object {
        private const val Key: String = "Key"
        private const val Value: String = "Value"
        private const val KeyWithNullValue: String = "KeyWithNullValue"
        private const val NonExistingKey: String = "NonExistingKey"
    }

    private val nonNullableMap = mapOf(Key to Value)
    private val nullableMap = mapOf(Key to Value, KeyWithNullValue to null)

    @Test fun inNonNullableMapGetOrNoneGets() = assertValue(Value, nonNullableMap.getOrNone(Key))
    @Test fun inNonNullableMapGetOrNoneOnlyRequiresOneCallToGet() =
        assertValue(Value, mockMap<_, String>(mootFunction, scriptedFunction(Key to Value)).getOrNone(Key))
    @Test fun inNonNullableMapGetOrNoneReturnsNone() = assertNone(nonNullableMap.getOrNone(NonExistingKey))
    @Test fun inNonNullableMapGetOrNoneOnlyRequiresOneCallToReturnNone() =
        assertNone(mockMap<_, String>(mootFunction, scriptedFunction(Key to null)).getOrNone(Key))

    @Test fun inNullableMapGetOrNoneGets() = assertValue(Value, nullableMap.getOrNone(Key))
    @Test fun inNullableMapGetOrNoneOnlyRequiresOneCallToGetNonNull() =
        assertValue(Value, mockMap<_, String?>(mootFunction, scriptedFunction(Key to Value)).getOrNone(Key))
    @Test fun inNullableMapGetOrNoneGetsNull() = assertValue(null, nullableMap.getOrNone(KeyWithNullValue))
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
