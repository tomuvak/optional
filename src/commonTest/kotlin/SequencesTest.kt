package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class SequencesTest {
    @Test fun valuesOfEmptySequence() = emptySequence<Optional<Int>>().whenValues { then() }
    @Test fun valuesOfSingletonNone() = sequenceOf(None).whenValues { then() }
    @Test fun valuesOfSingletonValue() = sequenceOf(Value(3)).whenValues { then(3) }
    @Test fun valuesOfMixedOptionals() = sequenceOf(Value(1), None, None, Value(2), None, Value(3), None).whenValues {
        then(1, 2, 3)
    }
    @Test fun valuesComputeLazily() = sequence {
        yield(Value(1))
        yield(None)
        yield(Value(2))
        yield(Value(3))
        fail("Not supposed to be attempted")
    }.whenValues { take(3).then(1, 2, 3) }

    @Test fun valuesUntilFirstNoneOfEmptySequence() = emptySequence<Optional<Int>>().whenValuesUntilFirstNone { then() }
    @Test fun valuesUntilFirstNoneOfSingletonNone() = sequenceOf(None).whenValuesUntilFirstNone { then() }
    @Test fun valuesUntilFirstNoneOfSingletonValue() = sequenceOf(Value(3)).whenValuesUntilFirstNone { then(3) }
    @Test fun valuesUntilFirstNoneWhenStartingWithNone() = sequence {
        yield(None)
        fail("Not supposed to be attempted")
    }.whenValuesUntilFirstNone { then() }
    @Test fun valuesUntilFirstNoneWhenNoNones() = sequenceOf(Value(1), Value(2), Value(3)).whenValuesUntilFirstNone {
        then(1, 2, 3)
    }
    @Test fun valuesUntilFirstNoneWhenMixedOptionals() =
        sequenceOf(Value(1), Value(2), Value(3), None, Value(4), None, Value(5), Value(6)).whenValuesUntilFirstNone {
            then(1, 2, 3)
        }
    @Test fun valuesUntilFirstNoneComputeLazily() = sequence {
        yield(Value(1))
        yield(Value(2))
        yield(Value(3))
        fail("Not supposed to be attempted")
    }.whenValuesUntilFirstNone { take(3).then(1, 2, 3) }

    private fun <T> Sequence<Optional<T>>.whenValues(continuation: Sequence<T>.() -> Unit) = values().continuation()
    private fun <T> Sequence<Optional<T>>.whenValuesUntilFirstNone(continuation: Sequence<T>.() -> Unit) =
        valuesUntilFirstNone().continuation()

    private fun <T> Sequence<T>.then(vararg expectedValues: T) = assertEquals(expectedValues.toList(), toList())
}
