package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.fail

class OptionalTest {
    @Test fun failsToForceNoneValue() { assertFails { None.forcedValue } }
    @Test fun forcesValue() = assertEquals(3, Value(3).forcedValue)

    @Test fun replacesNoneWithDefault() = assertEquals(5, None or 5)
    @Test fun doesNotReplaceValueWithDefault() = assertEquals(3, Value(3) or 5)

    @Test fun replacesNoneWithComputedDefault() = assertEquals(5, None or { 5 })
    @Test fun doesNotComputeDefaultWhenValue() =
        assertEquals(3, Value(3) or { fail("Not supposed to be called") })

    @Test fun replacesNoneWithDefaultNone() = assertNone(None orMaybe None)
    @Test fun replacesNoneWithDefaultValue() = assertValue(5, None orMaybe Value(5))
    @Test fun doesNotReplaceValueWithDefaultNone() = assertValue(3, Value(3) orMaybe None)
    @Test fun doesNotReplaceValueWithDefaultValue() = assertValue(3, Value(3) orMaybe Value(5))

    @Test fun replacesNoneWithComputedDefaultNone() = assertNone(None orMaybe { None })
    @Test fun replacesNoneWithComputedDefaultValue() = assertValue(5, None orMaybe { Value(5) })
    @Test fun doesNotComputeDefaultOptionalWhenValue() =
        assertValue(3, Value(3) orMaybe { fail("Not supposed to be called") })

    @Test fun mapsNone() = assertNone(None.map { fail("Not supposed to be called") })
    @Test fun mapsValue() = assertValue(5, Value(3).map {
        assertEquals(3, it)
        5
    })

    @Test fun flatMapsNone() = assertNone(None.flatMap<Int, String> { fail("Not supposed to be called") })
    @Test fun flatMapsToNone() = assertNone(Value(3).flatMap {
        assertEquals(3, it)
        None
    })
    @Test fun flatMapsToValue() = assertValue("success", Value(3).flatMap {
        assertEquals(3, it)
        Value("success")
    })

    @Test fun flattensOuterNone() = assertNone(None.flatten<Any?>())
    @Test fun flattensInnerNone() = assertNone(Value(None).flatten())
    @Test fun flattensValue() = assertValue(3, Value(Value(3)).flatten())
}
