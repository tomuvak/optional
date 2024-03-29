package com.tomuvak.optional.iterators

import com.tomuvak.optional.Optional
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.mootProvider
import com.tomuvak.testing.scriptedProvider
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

    @Test fun toIteratorReturnsOriginal() {
        val iterator = object : Iterator<Int> {
            override fun hasNext(): Boolean = mootProvider()
            override fun next(): Int = mootProvider()
        }
        assertSame(iterator, iterator.toOptionalBased().toIterator())
    }

    @Test fun toOptionalBased() {
        lateinit var mockHasNext: () -> Boolean
        lateinit var mockNext: () -> Int

        val iterator: Iterator<Int> = object : Iterator<Int> {
            override fun hasNext(): Boolean = mockHasNext()
            override fun next(): Int = mockNext()
        }

        val optionalBasedIterator = iterator.toOptionalBased()

        fun verifyNext(value: Int) {
            mockHasNext = scriptedProvider(true)
            mockNext = scriptedProvider(value)
            assertValue(value, optionalBasedIterator.nextOrNone())
        }

        fun verifyNoNext() {
            mockHasNext = scriptedProvider(false)
            mockNext = mootProvider
            assertNone(optionalBasedIterator.nextOrNone())
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
        val failureInHasNext = Exception("Failure in hasNext()")
        mockHasNext = { throw failureInHasNext }
        mockNext = mootProvider
        assertSame(failureInHasNext, assertFails { optionalBasedIterator.nextOrNone() })

        val failureInNext = Exception("Failure in next()")
        mockHasNext = scriptedProvider(true)
        mockNext = { throw failureInNext }
        assertSame(failureInNext, assertFails { optionalBasedIterator.nextOrNone() })
    }

    @Test fun toOptionalBasedReturnsOriginal() {
        val optionalBasedIterator = OptionalBasedIterator<Int>(mootProvider)
        assertSame(optionalBasedIterator, optionalBasedIterator.toIterator().toOptionalBased())
    }
}
