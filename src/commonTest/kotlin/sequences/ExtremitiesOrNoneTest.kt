package com.tomuvak.optional.sequences

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.MockFunction
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.testLazyTerminalOperation
import com.tomuvak.testing.testTerminalOperation
import kotlin.test.Test
import kotlin.test.assertEquals

class ExtremitiesOrNoneTest {
    private val alwaysFalse: MockFunction<Int, Boolean> = MockFunction { false }
    private val isEven: MockFunction<Int, Boolean> = MockFunction { it % 2 == 0 }
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
