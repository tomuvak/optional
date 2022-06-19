package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class OptionalTest {
    @Test fun failsToForceNoneValue() { assertFails { None.forcedValue } }
    @Test fun forcesValue() = assertEquals(3, Value(3).forcedValue)
}
