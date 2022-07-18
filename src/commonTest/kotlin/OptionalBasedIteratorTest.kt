package com.tomuvak.optional

import com.tomuvak.testing.assertions.scriptedProvider
import kotlin.test.*

class OptionalBasedIteratorTest {
    @Test fun toIterator() {
        lateinit var mockNextOrNone: () -> Optional<Int>
        val optionalBasedIterator = OptionalBasedIterator { mockNextOrNone() }
        val iterator = optionalBasedIterator.toIterator()

        fun verifyNext(value: Int) {
            mockNextOrNone = scriptedProvider(Optional.Value(value))
            assertTrue(iterator.hasNext())
            assertTrue(iterator.hasNext()) // Verify subsequent calls to .hasNext() also succeed (and don't prevent the
            assertTrue(iterator.hasNext()) // following call to .next() from behaving as expected either).
            assertEquals(value, iterator.next())
        }

        fun verifyNoNext() {
            mockNextOrNone = scriptedProvider(Optional.None)
            assertFalse(iterator.hasNext())
            assertFalse(iterator.hasNext()) // Verify subsequent calls to .hasNext() also succeed (and don't prevent the
            assertFalse(iterator.hasNext()) // following call to .next() from behaving as expected either).
            assertFailsWith<NoSuchElementException> { iterator.next() }
        }

        verifyNext(3)
        verifyNext(5)
        verifyNext(0)
        verifyNext(-7)
        verifyNext(-7)
        verifyNoNext()

        // Typical use cases do not include further iteration after exhaustion, but it is not impossible
        verifyNext(-1)
        verifyNext(0)
        verifyNext(0)
        verifyNext(1)
        verifyNoNext()

        // Verify expected behavior also in cases of failure
        val someFailure = Exception("some failure")
        mockNextOrNone = { throw someFailure }
        assertSame(someFailure, assertFails { iterator.hasNext() })

        val anotherFailure = Exception("another failure")
        mockNextOrNone = { throw anotherFailure }
        assertSame(anotherFailure, assertFails { iterator.next() })
    }
}
