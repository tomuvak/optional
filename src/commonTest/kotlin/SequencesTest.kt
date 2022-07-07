package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.testing.assertions.assertValues
import com.tomuvak.testing.assertions.testIntermediateOperation
import kotlin.test.Test
import kotlin.test.fail

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
    @Test fun valuesComputeLazily() = sequence {
        yield(Value(1))
        yield(None)
        yield(Value(2))
        yield(Value(3))
        fail("Not supposed to be attempted")
    }.testIntermediateOperation({ values() }) { take(3).assertValues(1, 2, 3) }

    @Test fun valuesUntilFirstNoneOfEmptySequence() =
        emptySequence<Optional<Int>>().testIntermediateOperation({ valuesUntilFirstNone() }) { assertValues() }
    @Test fun valuesUntilFirstNoneOfSingletonNone() =
        sequenceOf(None).testIntermediateOperation({ valuesUntilFirstNone() }) { assertValues() }
    @Test fun valuesUntilFirstNoneOfSingletonValue() =
        sequenceOf(Value(3)).testIntermediateOperation({ valuesUntilFirstNone() }) { assertValues(3) }
    @Test fun valuesUntilFirstNoneWhenStartingWithNone() = sequence {
        yield(None)
        fail("Not supposed to be attempted")
    }.testIntermediateOperation({ valuesUntilFirstNone() }) { assertValues() }
    @Test fun valuesUntilFirstNoneWhenNoNones() = sequenceOf(Value(1), Value(2), Value(3)).testIntermediateOperation(
        { valuesUntilFirstNone() }
    ) { assertValues(1, 2, 3) }
    @Test fun valuesUntilFirstNoneWhenMixedOptionals() =
        sequenceOf(Value(1), Value(2), Value(3), None, Value(4), None, Value(5), Value(6)).testIntermediateOperation(
            { valuesUntilFirstNone() }
        ) { assertValues(1, 2, 3) }
    @Test fun valuesUntilFirstNoneWhenMixedOptionalNullables() =
        sequenceOf(Value(1), Value(null), Value(2), None, Value(3), Value(4)).testIntermediateOperation(
            { valuesUntilFirstNone() }
        ) { assertValues(1, null, 2) }
    @Test fun valuesUntilFirstNoneComputeLazily() = sequence {
        yield(Value(1))
        yield(Value(2))
        yield(Value(3))
        fail("Not supposed to be attempted")
    }.testIntermediateOperation({ valuesUntilFirstNone() }) { take(3).assertValues(1, 2, 3) }
}
