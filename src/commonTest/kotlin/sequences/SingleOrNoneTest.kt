package com.tomuvak.optional.sequences

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.MockFunction
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.testLazyTerminalOperation
import com.tomuvak.testing.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SingleOrNoneTest {
    private val alwaysFalse: MockFunction<Int, Boolean> = MockFunction { false }
    private val isThree: MockFunction<Int, Boolean> = MockFunction { it == 3 }
    private val isNull: MockFunction<Int?, Boolean> = MockFunction { it == null }

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
}
