package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootPredicate
import com.tomuvak.testing.assertions.testLazyTerminalOperation
import com.tomuvak.testing.assertions.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals

class SequencesTest {
    @Test fun firstOrNoneWhenEmpty() = emptySequence<Int>().testTerminalOperation({ firstOrNone() }, ::assertNone)
    @Test fun firstOrNoneWhenValue() = sequenceOf(3).testLazyTerminalOperation({ firstOrNone() }) { assertValue(3, it) }
    @Test fun firstOrNoneWhenNullValue() =
        sequenceOf(null).testLazyTerminalOperation({ firstOrNone() }) { assertValue(null, it) }

    @Test fun firstOrNoneWithPredicateWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ firstOrNone(mootPredicate) }, ::assertNone)
    @Test fun firstOrNoneWithPredicateWhenNone() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ firstOrNone {
            testedElements.add(it)
            false
        } }, ::assertNone)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun firstOrNoneWithPredicateWhenMatchingValue() = sequenceOf(1, 2, 3).testLazyTerminalOperation(
        { firstOrNone { it == 3 } }
    ) { assertValue(3, it) }
    @Test fun firstOrNoneWithPredicateWhenMatchingNullValue() = sequenceOf(1, 2, null).testLazyTerminalOperation(
        { firstOrNone { it == null } }
    ) { assertValue(null, it) }

    @Test fun lastOrNoneWhenEmpty() = emptySequence<Int>().testTerminalOperation({ lastOrNone() }, ::assertNone)
    @Test fun lastOrNoneWhenValue() = sequenceOf(1, 2, 3).testTerminalOperation({ lastOrNone() }) { assertValue(3, it) }
    @Test fun lastOrNoneWhenNullValue() =
        sequenceOf(1, 2, null).testTerminalOperation({ lastOrNone() }) { assertValue(null, it) }

    @Test fun lastOrNoneWithPredicateWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ lastOrNone(mootPredicate) }, ::assertNone)
    @Test fun lastOrNoneWithPredicateWhenNone() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ lastOrNone {
            testedElements.add(it)
            false
        } }, ::assertNone)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun lastOrNoneWithPredicateWhenMatchingValues() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ lastOrNone {
            testedElements.add(it)
            it % 2 == 0
        } }) { assertValue(4, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun lastOrNoneWithPredicateWhenMatchingValuesIncludingNulls() {
        val testedElements = mutableListOf<Int?>()
        sequenceOf(1, 2, 3, null, 5).testTerminalOperation({ lastOrNone {
            testedElements.add(it)
            it == 2 || it == null
        } }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, 3, null, 5), testedElements)
    }

    @Test fun singleNoVerifyOrNoneWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleNoVerifyOrNone() }, ::assertNone)
    @Test fun singleNoVerifyOrNoneWhenValue() =
        sequenceOf(3).testLazyTerminalOperation({ singleNoVerifyOrNone() }) { assertValue(3, it) }
    @Test fun singleNoVerifyOrNoneWhenNullValue() =
        sequenceOf(null).testLazyTerminalOperation({ singleNoVerifyOrNone() }) { assertValue(null, it) }

    @Test fun singleNoVerifyOrNoneWithPredicateWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleNoVerifyOrNone(mootPredicate) }, ::assertNone)
    @Test fun singleNoVerifyOrNoneWithPredicateWhenNone() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ singleNoVerifyOrNone {
            testedElements.add(it)
            false
        } }, ::assertNone)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleNoVerifyOrNoneWithPredicateWhenMatchingValue() = sequenceOf(1, 2, 3).testLazyTerminalOperation(
        { singleNoVerifyOrNone { it == 3 } }
    ) { assertValue(3, it) }
    @Test fun singleNoVerifyOrNoneWithPredicateWhenMatchingNullValue() =
        sequenceOf(1, 2, null).testLazyTerminalOperation({ singleNoVerifyOrNone { it == null } }) {
            assertValue(null, it)
        }
}
