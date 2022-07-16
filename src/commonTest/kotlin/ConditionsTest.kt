package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ConditionsTest {
    @Test fun thenYieldsNoneWhenFalse() = assertNone(false.then(3))
    @Test fun thenYieldsValueWhenTrue() = assertValue(3, true.then(3))

    @Test fun thenWithProviderWhenFalse() = assertNone(false.then { fail("Not supposed to be called") })
    @Test fun thenWithProviderWhenTrue() {
        var numCalls = 0
        assertValue(3, true.then {
            numCalls++
            3
        })
        assertEquals(1, numCalls)
    }
}
