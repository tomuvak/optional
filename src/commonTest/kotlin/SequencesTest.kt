package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootPredicate
import com.tomuvak.testing.assertions.testLazyTerminalOperation
import com.tomuvak.testing.assertions.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

    @Test fun singleOrNoneIfEmptyReturnsNoneWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNoneIfEmpty() }, ::assertNone)
    @Test fun singleOrNoneIfEmptyReturnsValueWhenSingle() =
        sequenceOf(3).testTerminalOperation({ singleOrNoneIfEmpty() }) { assertValue(3, it) }
    @Test fun singleOrNoneIfEmptyReturnsNullValueWhenSingle() =
        sequenceOf(null).testTerminalOperation({ singleOrNoneIfEmpty() }) { assertValue(null, it) }
    @Test fun singleOrNoneIfEmptyThrowsWhenMultiple() = sequenceOf(3, 3).testLazyTerminalOperation({
        assertFailsWith<IllegalArgumentException> { singleOrNoneIfEmpty() }
    })

    @Test fun singleOrNoneIfNoneReturnsNoneWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNoneIfNone(mootPredicate) }, ::assertNone)
    @Test fun singleOrNoneIfNoneReturnsNoneWhenNone() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNoneIfNone {
            testedElements.add(it)
            false
        }}, ::assertNone)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNoneIfNoneReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNoneIfNone {
            testedElements.add(it)
            it == 3
        }}) { assertValue(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun singleOrNoneIfNoneReturnsNullValueWhenSingle() {
        val testedElements = mutableListOf<Int?>()
        sequenceOf(1, 2, null, 4, 5).testTerminalOperation({ singleOrNoneIfNone {
            testedElements.add(it)
            it == null
        }}) { assertValue(null, it) }
        assertEquals(listOf(1, 2, null, 4, 5), testedElements)
    }
    @Test fun singleOrNoneIfNoneThrowsWhenMultiple() = sequenceOf(1, 2, 3, 3).testLazyTerminalOperation({
        assertFailsWith<IllegalArgumentException> { singleOrNoneIfNone { it == 3 } }
    })

    @Test fun singleOrNoneIfMultipleThrowsWhenEmpty() = emptySequence<Int>().testTerminalOperation({
        assertFailsWith<NoSuchElementException> { singleOrNoneIfMultiple() }
    })
    @Test fun singleOrNoneIfMultipleReturnsValueWhenSingle() =
        sequenceOf(3).testTerminalOperation({ singleOrNoneIfMultiple() }) { assertValue(3, it) }
    @Test fun singleOrNoneIfMultipleReturnsNullValueWhenSingle() =
        sequenceOf(null).testTerminalOperation({ singleOrNoneIfMultiple() }) { assertValue(null, it) }
    @Test fun singleOrNoneIfMultipleReturnsNoneWhenMultiple() =
        sequenceOf(3, 3).testLazyTerminalOperation({ singleOrNoneIfMultiple() }, ::assertNone)

    @Test fun singleOrNoneIfMultipleWithPredicateThrowsWhenEmpty() = emptySequence<Int>().testTerminalOperation({
        assertFailsWith<NoSuchElementException> { singleOrNoneIfMultiple(mootPredicate) }
    })
    @Test fun singleOrNoneIfMultipleWithPredicateThrowsWhenNone() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({
            assertFailsWith<NoSuchElementException> { singleOrNoneIfMultiple {
                testedElements.add(it)
                false
            } }
        })
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNoneIfMultipleWithPredicateReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNoneIfMultiple {
            testedElements.add(it)
            it == 3
        } }) { assertValue(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun singleOrNoneIfMultipleWithPredicateReturnsNullValueWhenSingle() {
        val testedElements = mutableListOf<Int?>()
        sequenceOf(1, 2, null, 4, 5).testTerminalOperation({ singleOrNoneIfMultiple {
            testedElements.add(it)
            it == null
        } }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, null, 4, 5), testedElements)
    }
    @Test fun singleOrNoneIfMultipleWithPredicateReturnsNoneWhenMultiple() =
        sequenceOf(1, 2, 3, 3).testLazyTerminalOperation({ singleOrNoneIfMultiple { it == 3 } }, ::assertNone)

    @Test fun singleOrNoneIfEmptyOrMultipleReturnsNoneWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNoneIfEmptyOrMultiple() }, ::assertNone)
    @Test fun singleOrNoneIfEmptyOrMultipleReturnsValueWhenSingle() =
        sequenceOf(3).testTerminalOperation({ singleOrNoneIfEmptyOrMultiple() }) { assertValue(3, it) }
    @Test fun singleOrNoneIfEmptyOrMultipleReturnsNullValueWhenSingle() =
        sequenceOf(null).testTerminalOperation({ singleOrNoneIfEmptyOrMultiple() }) { assertValue(null, it) }
    @Test fun singleOrNoneIfEmptyOrMultipleReturnsNoneWhenMultiple() =
        sequenceOf(3, 3).testLazyTerminalOperation({ singleOrNoneIfEmptyOrMultiple() }, ::assertNone)

    @Test fun singleOrNoneIfNoneOrMultipleReturnsNoneWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ singleOrNoneIfNoneOrMultiple(mootPredicate) }, ::assertNone)
    @Test fun singleOrNoneIfNoneOrMultipleReturnsNoneWhenNone() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNoneIfNoneOrMultiple {
            testedElements.add(it)
            false
        } }, ::assertNone)
        assertEquals(listOf(1, 2, 3), testedElements)
    }
    @Test fun singleOrNoneIfNoneOrMultipleReturnsValueWhenSingle() {
        val testedElements = mutableListOf<Int>()
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNoneIfNoneOrMultiple {
            testedElements.add(it)
            it == 3
        } }) { assertValue(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), testedElements)
    }
    @Test fun singleOrNoneIfNoneOrMultipleReturnsNullValueWhenSingle() {
        val testedElements = mutableListOf<Int?>()
        sequenceOf(1, 2, null, 4, 5).testTerminalOperation({ singleOrNoneIfNoneOrMultiple {
            testedElements.add(it)
            it == null
        } }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, null, 4, 5), testedElements)
    }
    @Test fun singleOrNoneIfNoneOrMultipleReturnsNoneWhenMultiple() =
        sequenceOf(1, 2, 3, 3).testLazyTerminalOperation({ singleOrNoneIfNoneOrMultiple { it == 3 }}, ::assertNone)

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
