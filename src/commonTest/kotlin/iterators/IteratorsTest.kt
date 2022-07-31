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
        mockHasNext = scriptedProvider(false)
        mockNext = mootProvider
        assertNone(iterator.nextOrNone())
    }

    @Test fun nextOrNoneReturnsNext() {
        mockHasNext = scriptedProvider(true)
        mockNext = scriptedProvider(3)
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
}
