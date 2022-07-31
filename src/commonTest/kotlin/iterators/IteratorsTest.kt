package com.tomuvak.optional.iterators

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootProvider
import com.tomuvak.testing.assertions.scriptedProvider
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertSame

class IteratorsTest {
    private lateinit var mockHasNext: () -> Boolean
    private lateinit var mockNext: () -> Int

    private val iterator: Iterator<Int> = object : Iterator<Int> {
        override fun hasNext(): Boolean = mockHasNext()
        override fun next(): Int = mockNext()
    }

    @Test fun nextOrNoneReturnsNone() {
        givenNoNext()
        assertNone(iterator.nextOrNone())
    }

    @Test fun nextOrNoneReturnsNext() {
        givenNext(3)
        assertValue(3, iterator.nextOrNone())
    }

    @Test fun nextOrNoneFailsWhenHasNextFails() {
        val failure = Exception("Failure in hasNext()")
        mockHasNext = { throw failure }
        mockNext = mootProvider
        assertSame(failure, assertFails { iterator.nextOrNone() })
    }

    @Test fun nextOrNoneFailsWhenNextFails() {
        val failure = Exception("Failure in next()")
        mockHasNext = scriptedProvider(true)
        mockNext = { throw failure }
        assertSame(failure, assertFails { iterator.nextOrNone() })
    }

    @Test fun toOptionalBased() {
        val optionalBasedIterator = iterator.toOptionalBased()

        fun verifyNext(value: Int) {
            givenNext(value)
            assertValue(value, optionalBasedIterator.nextOrNone())
        }

        fun verifyNoNext() {
            givenNoNext()
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

    private fun givenNoNext() {
        mockHasNext = scriptedProvider(false)
        mockNext = mootProvider
    }

    private fun givenNext(value: Int) {
        mockHasNext = scriptedProvider(true)
        mockNext = scriptedProvider(value)
    }
}
