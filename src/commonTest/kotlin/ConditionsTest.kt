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

    @Test fun ifSatisfiesWhenDoesNotSatisfy() {
        val testedArguments = mutableListOf<Int>()
        assertNone(3.ifSatisfies {
            testedArguments.add(it)
            false
        })
        assertEquals(listOf(3), testedArguments)
    }

    @Test fun ifSatisfiesWhenSatisfies() {
        val testedArguments = mutableListOf<Int>()
        assertValue(
            3,
            3.ifSatisfies {
                testedArguments.add(it)
                true
            }
        )
        assertEquals(listOf(3), testedArguments)
    }
}
