package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.*
import kotlin.test.Test

class SequencesTest {
    @Test fun valuesOfEmptySequence() =
        emptySequence<Optional<Int>>().testIntermediateOperation({ values() }) { assertValues() }
    @Test fun valuesOfSingletonNone() = sequenceOf(None).testIntermediateOperation({ values() }) { assertValues() }
    @Test fun valuesOfSingletonValue() =
        sequenceOf(Value(3)).testIntermediateOperation({ values() }) { assertValues(3) }
    @Test fun valuesOfMixedOptionals() =
        sequenceOf(Value(1), None, None, Value(2), None, Value(3), None).testIntermediateOperation({ values() }) {
            assertValues(1, 2, 3)
        }
    @Test fun valuesOfMixedOptionalNullables() =
        sequenceOf(Value(1), None, Value(null), Value(2), Value(null), Value(3), None).testIntermediateOperation(
            { values() }
        ) { assertValues(1, null, 2, null, 3) }
    @Test fun valuesComputeLazily() = sequenceOf(Value(1), None, Value(2), Value(3)).testLazyIntermediateOperation({
        values()
    }) { assertStartsWith(1, 2, 3) }

    @Test fun valuesUntilFirstNoneOfEmptySequence() =
        emptySequence<Optional<Int>>().testIntermediateOperation({ valuesUntilFirstNone() }) { assertValues() }
    @Test fun valuesUntilFirstNoneWhenStartingWithNone() = sequenceOf(None).testLazyIntermediateOperation({
        valuesUntilFirstNone()
    }) { assertValues() }
    @Test fun valuesUntilFirstNoneOfSingletonValue() =
        sequenceOf(Value(3)).testIntermediateOperation({ valuesUntilFirstNone() }) { assertValues(3) }
    @Test fun valuesUntilFirstNoneWhenNoNones() = sequenceOf(Value(1), Value(2), Value(3)).testIntermediateOperation(
        { valuesUntilFirstNone() }
    ) { assertValues(1, 2, 3) }
    @Test fun valuesUntilFirstNoneWhenMixedOptionals() =
        sequenceOf(Value(1), Value(2), Value(3), None).testLazyIntermediateOperation(
            { valuesUntilFirstNone() }
        ) { assertValues(1, 2, 3) }
    @Test fun valuesUntilFirstNoneWhenMixedOptionalNullables() =
        sequenceOf(Value(1), Value(null), Value(2), None).testLazyIntermediateOperation(
            { valuesUntilFirstNone() }
        ) { assertValues(1, null, 2) }
    @Test fun valuesUntilFirstNoneComputeLazily() =
        sequenceOf(Value(1), Value(2), Value(3)).testLazyIntermediateOperation({ valuesUntilFirstNone() }) {
            assertStartsWith(1, 2, 3)
        }

    @Test fun valuesIfAllWhenEmpty() = emptySequence<Optional<Int>>().testTerminalOperation({ valuesIfAll() }) {
        assertValue(emptyList(), it)
    }
    @Test fun valuesIfAllWhenAll() = sequenceOf(Value(1), Value(2), Value(3)).testTerminalOperation({ valuesIfAll() }) {
        assertValue(listOf(1, 2, 3), it)
    }
    @Test fun valuesIfAllWhenNotAll() =
        sequenceOf(Value(1), None).testLazyTerminalOperation({ valuesIfAll() }, ::assertNone)
}
