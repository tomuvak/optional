package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.MockFunction
import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.testLazyTerminalOperation
import com.tomuvak.testing.assertions.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SequencesTest {
    private val alwaysFalse: MockFunction<Int, Boolean> = MockFunction { false }
    private val isThree: MockFunction<Int, Boolean> = MockFunction { it == 3 }
    private val isEven: MockFunction<Int, Boolean> = MockFunction { it % 2 == 0 }
    private val isNull: MockFunction<Int?, Boolean> = MockFunction { it == null }
    private val isTwoOrNull: MockFunction<Int?, Boolean> = MockFunction { it == 2 || it == null }

    @Test fun firstOrNoneWhenEmpty() = emptySequence<Int>().testTerminalOperation({ firstOrNone() }, ::assertNone)
    @Test fun firstOrNoneWhenValue() = sequenceOf(3).testLazyTerminalOperation({ firstOrNone() }) { assertValue(3, it) }
    @Test fun firstOrNoneWhenNullValue() =
        sequenceOf(null).testLazyTerminalOperation({ firstOrNone() }) { assertValue(null, it) }

    @Test fun firstOrNoneWithPredicateWhenEmpty() =
        emptySequence<Int>().testTerminalOperation({ firstOrNone(mootFunction) }, ::assertNone)
    @Test fun firstOrNoneWithPredicateWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ firstOrNone(alwaysFalse::invoke) }, ::assertNone)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
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
        emptySequence<Int>().testTerminalOperation({ lastOrNone(mootFunction) }, ::assertNone)
    @Test fun lastOrNoneWithPredicateWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ lastOrNone(alwaysFalse::invoke) }, ::assertNone)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun lastOrNoneWithPredicateWhenMatchingValues() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ lastOrNone(isEven::invoke) }) { assertValue(4, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isEven.calls)
    }
    @Test fun lastOrNoneWithPredicateWhenMatchingValuesIncludingNulls() {
        sequenceOf(1, 2, 3, null, 5).testTerminalOperation({
            lastOrNone(isTwoOrNull::invoke)
        }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, 3, null, 5), isTwoOrNull.calls)
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
        emptySequence<Int>().testTerminalOperation({ singleOrNoneIfNone(mootFunction) }, ::assertNone)
    @Test fun singleOrNoneIfNoneReturnsNoneWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNoneIfNone(alwaysFalse::invoke) }, ::assertNone)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleOrNoneIfNoneReturnsValueWhenSingle() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({ singleOrNoneIfNone(isThree::invoke) }) { assertValue(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isThree.calls)
    }
    @Test fun singleOrNoneIfNoneReturnsNullValueWhenSingle() {
        sequenceOf(1, 2, null, 4, 5).testTerminalOperation({
            singleOrNoneIfNone(isNull::invoke)
        }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, null, 4, 5), isNull.calls)
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
        assertFailsWith<NoSuchElementException> { singleOrNoneIfMultiple(mootFunction) }
    })
    @Test fun singleOrNoneIfMultipleWithPredicateThrowsWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({
            assertFailsWith<NoSuchElementException> { singleOrNoneIfMultiple(alwaysFalse::invoke) }
        })
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleOrNoneIfMultipleWithPredicateReturnsValueWhenSingle() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({
            singleOrNoneIfMultiple(isThree::invoke)
        }) { assertValue(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isThree.calls)
    }
    @Test fun singleOrNoneIfMultipleWithPredicateReturnsNullValueWhenSingle() {
        sequenceOf(1, 2, null, 4, 5).testTerminalOperation({
            singleOrNoneIfMultiple(isNull::invoke)
        }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, null, 4, 5), isNull.calls)
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
        emptySequence<Int>().testTerminalOperation({ singleOrNoneIfNoneOrMultiple(mootFunction) }, ::assertNone)
    @Test fun singleOrNoneIfNoneOrMultipleReturnsNoneWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ singleOrNoneIfNoneOrMultiple(alwaysFalse::invoke) }, ::assertNone)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleOrNoneIfNoneOrMultipleReturnsValueWhenSingle() {
        sequenceOf(1, 2, 3, 4, 5).testTerminalOperation({
            singleOrNoneIfNoneOrMultiple(isThree::invoke)
        }) { assertValue(3, it) }
        assertEquals(listOf(1, 2, 3, 4, 5), isThree.calls)
    }
    @Test fun singleOrNoneIfNoneOrMultipleReturnsNullValueWhenSingle() {
        sequenceOf(1, 2, null, 4, 5).testTerminalOperation({
            singleOrNoneIfNoneOrMultiple(isNull::invoke)
        }) { assertValue(null, it) }
        assertEquals(listOf(1, 2, null, 4, 5), isNull.calls)
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
        emptySequence<Int>().testTerminalOperation({ singleNoVerifyOrNone(mootFunction) }, ::assertNone)
    @Test fun singleNoVerifyOrNoneWithPredicateWhenNone() {
        sequenceOf(1, 2, 3).testTerminalOperation({ singleNoVerifyOrNone(alwaysFalse::invoke) }, ::assertNone)
        assertEquals(listOf(1, 2, 3), alwaysFalse.calls)
    }
    @Test fun singleNoVerifyOrNoneWithPredicateWhenMatchingValue() = sequenceOf(1, 2, 3).testLazyTerminalOperation(
        { singleNoVerifyOrNone { it == 3 } }
    ) { assertValue(3, it) }
    @Test fun singleNoVerifyOrNoneWithPredicateWhenMatchingNullValue() =
        sequenceOf(1, 2, null).testLazyTerminalOperation({ singleNoVerifyOrNone { it == null } }) {
            assertValue(null, it)
        }
}
