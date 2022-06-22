package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class SequencesTest {
    @Test fun valuesOfEmptySequence() = emptySequence<Optional<Int>>().test({ values() }) { then() }
    @Test fun valuesOfSingletonNone() = sequenceOf(None).test({ values() }) { then() }
    @Test fun valuesOfSingletonValue() = sequenceOf(Value(3)).test({ values() }) { then(3) }
    @Test fun valuesOfMixedOptionals() = sequenceOf(Value(1), None, None, Value(2), None, Value(3), None).test(
        { values() }
    ) { then(1, 2, 3) }
    @Test fun valuesComputeLazily() = sequence {
        yield(Value(1))
        yield(None)
        yield(Value(2))
        yield(Value(3))
        fail("Not supposed to be attempted")
    }.test({ values() }) { take(3).then(1, 2, 3) }

    @Test fun valuesUntilFirstNoneOfEmptySequence() = emptySequence<Optional<Int>>().test({ valuesUntilFirstNone() }) {
        then()
    }
    @Test fun valuesUntilFirstNoneOfSingletonNone() = sequenceOf(None).test({ valuesUntilFirstNone() }) { then() }
    @Test fun valuesUntilFirstNoneOfSingletonValue() = sequenceOf(Value(3)).test({ valuesUntilFirstNone() }) { then(3) }
    @Test fun valuesUntilFirstNoneWhenStartingWithNone() = sequence {
        yield(None)
        fail("Not supposed to be attempted")
    }.test({ valuesUntilFirstNone() }) { then() }
    @Test fun valuesUntilFirstNoneWhenNoNones() = sequenceOf(Value(1), Value(2), Value(3)).test(
        { valuesUntilFirstNone() }
    ) { then(1, 2, 3) }
    @Test fun valuesUntilFirstNoneWhenMixedOptionals() =
        sequenceOf(Value(1), Value(2), Value(3), None, Value(4), None, Value(5), Value(6)).test(
            { valuesUntilFirstNone() }
        ) { then(1, 2, 3) }
    @Test fun valuesUntilFirstNoneComputeLazily() = sequence {
        yield(Value(1))
        yield(Value(2))
        yield(Value(3))
        fail("Not supposed to be attempted")
    }.test({ valuesUntilFirstNone() }) { take(3).then(1, 2, 3) }

    /**
     * Verifies sequences obtained by running the given [operation] pass the given [continuation] block.
     *
     * The receiver sequence [this] is expected to be reiterable, and this function verifies additionally that the
     * sequence obtained by [operation] is also reiterable, and passes [continuation] a second time as well.
     *
     * This function also verifies that the result of running [operation] on the [constrainOnce] version of [this]
     * passes the given [continuation], and also that it is not reiterable.
     */
    private fun <T, R> Sequence<T>.test(
        operation: Sequence<T>.() -> Sequence<R>,
        continuation: Sequence<R>.() -> Unit
    ) {
        val result = try { operation() } catch (e: Throwable) { fail("The operation failed", e) }
        try { result.continuation() } catch (e: Throwable) { fail("The operation failed to pass the test", e) }
        try { result.continuation() } catch (e: Throwable) {
            fail("The operation yields a sequence which does not behave as expected when reiterated", e)
        }
        val constrainedOnce = try { constrainOnce().operation() } catch (e: Throwable) {
            fail("The operation failed on a constrained-once sequence", e)
        }
        try { constrainedOnce.continuation() } catch (e: Throwable) {
            fail("The operation failed to pass the test for a constrained-once sequence", e)
        }
        assertFailsWith<IllegalStateException>(
            "The operation on a constrained-once sequence yields a seemingly reiterable sequence"
        ) { constrainedOnce.iterator() }
    }

    private fun <T> Sequence<T>.then(vararg expectedValues: T) = assertEquals(expectedValues.toList(), toList())
}
