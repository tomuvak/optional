package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedFunction
import kotlin.test.Test
import kotlin.test.assertEquals

class ConditionsTest {
    @Test fun thenYieldsNoneWhenFalse() = assertNone(false.then(3))
    @Test fun thenYieldsValueWhenTrue() = assertValue(3, true.then(3))

    @Test fun thenWithProviderWhenFalse() = assertNone(false.then(mootProvider))
    @Test fun thenWithProviderWhenTrue() {
        var numCalls = 0
        assertValue(3, true.then {
            numCalls++
            3
        })
        assertEquals(1, numCalls)
    }

    @Test fun ifSatisfiesWhenDoesNotSatisfy() = assertNone(3.ifSatisfies(scriptedFunction(3 to false)))
    @Test fun ifSatisfiesWhenSatisfies() = assertValue(3, 3.ifSatisfies(scriptedFunction(3 to true)))
}
