package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootFunction
import kotlin.test.Test
import kotlin.test.assertEquals

class ContainerTest {
    @Test fun mapsNone() = assertNone(None.map(mootFunction))
    @Test fun mapsValue() = assertValue(5, Value(3).map {
        assertEquals(3, it)
        5
    })

    @Test fun flatMapsNone() = assertNone(None.flatMap<Int, String>(mootFunction))
    @Test fun flatMapsToNone() = assertNone(Value(3).flatMap {
        assertEquals(3, it)
        None
    })
    @Test fun flatMapsToValue() = assertValue("success", Value(3).flatMap {
        assertEquals(3, it)
        Value("success")
    })

    @Test fun flattensOuterNone() = assertNone(None.flatten<Any?>())
    @Test fun flattensInnerNone() = assertNone(Value(None).flatten())
    @Test fun flattensValue() = assertValue(3, Value(Value(3)).flatten())

    @Test fun filterNone() = assertNone(None.filter(mootFunction))
    @Test fun filterFalseValue() = assertNone(Value(3).filter {
        assertEquals(3, it)
        false
    })
    @Test fun filterTrueValue() = assertValue(3, Value(3).filter {
        assertEquals(3, it)
        true
    })

    @Test fun filterNotNone() = assertNone(None.filterNot(mootFunction))
    @Test fun filterNotFalseValue() = assertValue(3, Value(3).filterNot {
        assertEquals(3, it)
        false
    })
    @Test fun filterNotTrueValue() = assertNone(Value(3).filterNot {
        assertEquals(3, it)
        true
    })

    @Test fun asEmptySequence() = assertEquals(emptyList(), None.asSequence().toList())
    @Test fun asSingletonSequence() = assertEquals(listOf(3), Value(3).asSequence().toList())

    @Test fun toEmptyList() = assertEquals(emptyList(), None.toList())
    @Test fun toSingletonList() = assertEquals(listOf(3), Value(3).toList())
}
