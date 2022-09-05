package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.mootProvider
import com.tomuvak.testing.scriptedFunction
import kotlin.test.Test

class MapsTest {
    companion object {
        private const val Key: String = "Key"
        private const val Value: String = "Value"
        private const val KeyWithNullValue: String = "KeyWithNullValue"
        private const val NonExistingKey: String = "NonExistingKey"
    }

    private val nonNullableMap: Map<String, String> get() = mapOf(Key to Value)
    private val nullableMap: Map<String, String?> get() = mapOf(Key to Value, KeyWithNullValue to null)

    @Test fun inNonNullableMapGetOrNoneGets() = assertValue(Value, nonNullableMap.getOrNone(Key))
    @Test fun inNonNullableMapGetOrNoneOnlyRequiresOneCallToGet() =
        assertValue(Value, oneCallMap<String>(Value).getOrNone(Key))
    @Test fun inNonNullableMapGetOrNoneReturnsNone() = assertNone(nonNullableMap.getOrNone(NonExistingKey))
    @Test fun inNonNullableMapGetOrNoneOnlyRequiresOneCallToReturnNone() =
        assertNone(oneCallMap<String>(null).getOrNone(Key))

    @Test fun inNullableMapGetOrNoneGets() = assertValue(Value, nullableMap.getOrNone(Key))
    @Test fun inNullableMapGetOrNoneOnlyRequiresOneCallToGetNonNull() =
        assertValue(Value, oneCallMap<String?>(Value).getOrNone(Key))
    @Test fun inNullableMapGetOrNoneGetsNull() = assertValue(null, nullableMap.getOrNone(KeyWithNullValue))
    @Test fun inNullableMapGetOrNoneReturnsNone() = assertNone(nullableMap.getOrNone(NonExistingKey))

    private fun <V> oneCallMap(valueOrNull: V?): Map<String, V> = object : Map<String, V> {
        private val getFunction: (String) -> V? = scriptedFunction(Key to valueOrNull)
        override fun get(key: String): V? = getFunction(key)

        override val entries: Set<Map.Entry<String, V>> get() = mootProvider()
        override val keys: Set<String> get() = mootProvider()
        override val size: Int get() = mootProvider()
        override val values: Collection<V> get() = mootProvider()
        override fun isEmpty(): Boolean = mootProvider()
        override fun containsValue(value: V): Boolean = mootFunction(value)
        override fun containsKey(key: String): Boolean = mootFunction(key)
    }
}
