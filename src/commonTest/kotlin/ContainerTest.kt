package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.assertions.mootFunction
import com.tomuvak.testing.assertions.scriptedFunction
import kotlin.test.Test
import kotlin.test.assertEquals

class ContainerTest {
    @Test fun mapsNone() = assertNone(None.map(mootFunction))
    @Test fun mapsValue() = assertValue(5, Value(3).map(scriptedFunction(3 to 5)))

    @Test fun flatMapsNone() = assertNone(None.flatMap<Int, String>(mootFunction))
    @Test fun flatMapsToNone() = assertNone(Value(3).flatMap(scriptedFunction(3 to None)))
    @Test fun flatMapsToValue() = assertValue("success", Value(3).flatMap(scriptedFunction(3 to Value("success"))))

    @Test fun flattensOuterNone() = assertNone(None.flatten<Any?>())
    @Test fun flattensInnerNone() = assertNone(Value(None).flatten())
    @Test fun flattensValue() = assertValue(3, Value(Value(3)).flatten())

    @Test fun filterNone() = assertNone(None.filter(mootFunction))
    @Test fun filterFalseValue() = assertNone(Value(3).filter(scriptedFunction(3 to false)))
    @Test fun filterTrueValue() = assertValue(3, Value(3).filter(scriptedFunction(3 to true)))

    @Test fun filterNotNone() = assertNone(None.filterNot(mootFunction))
    @Test fun filterNotFalseValue() = assertValue(3, Value(3).filterNot(scriptedFunction(3 to false)))
    @Test fun filterNotTrueValue() = assertNone(Value(3).filterNot(scriptedFunction(3 to true)))

    @Test fun asEmptySequence() = assertEquals(emptyList(), None.asSequence().toList())
    @Test fun asSingletonSequence() = assertEquals(listOf(3), Value(3).asSequence().toList())

    @Test fun asEmptyIterable() = assertEquals(emptyList(), None.asIterable().toList())
    @Test fun asSingletonIterable() = assertEquals(listOf(3), Value(3).asIterable().toList())

    @Test fun toEmptyList() = assertEquals(emptyList(), None.toList())
    @Test fun toSingletonList() = assertEquals(listOf(3), Value(3).toList())

    @Test fun toEmptyTypedArray() = assertEquals(emptyList(), (None as Optional<Int>).toTypedArray().toList())
    @Test fun toSingletonTypedArray() = assertEquals(listOf(3), Value(3).toTypedArray().toList())
}
