package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.MockFunction
import com.tomuvak.testing.mootFunction
import com.tomuvak.testing.mootProvider
import com.tomuvak.testing.scriptedFunction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class BasicTest {
    @Test fun failsToForceNoneValue() { assertFails { None.forcedValue } }
    @Test fun forcesValue() = assertEquals(3, Value(3).forcedValue)

    @Test fun failsToForceNoneValueWithCustomException() {
        val e = Exception("Some specific exception")
        assertEquals(e, assertFails { None.forcedValue { e } } )
    }
    @Test fun forcesValueWithCustomException() = assertEquals(3, Value(3).forcedValue(mootProvider))

    @Test fun switchWithValueOnNone() = assertEquals(3, None.switch(3, mootFunction))
    @Test fun switchWithValueOnValue() = assertEquals(4, Value(3).switch(5, scriptedFunction(3 to 4)))
    @Test fun switchWithProviderOnNone() = assertEquals(3, None.switch({ 3 }, mootFunction))
    @Test fun switchWithProviderOnValue() = assertEquals(4, Value(3).switch(mootFunction, scriptedFunction(3 to 4)))

    @Test fun doesNotRunOnValueOnNone() = None.runOnValue(mootFunction)
    @Test fun runsOnValue() {
        val mockFunction = MockFunction<Int, Unit> {}
        Value(3).runOnValue(mockFunction::invoke)
        assertEquals(listOf(3), mockFunction.calls)
    }

    @Test fun replacesNoneWithDefault() = assertEquals(5, None or 5)
    @Test fun doesNotReplaceValueWithDefault() = assertEquals(3, Value(3) or 5)

    @Test fun replacesNoneWithComputedDefault() = assertEquals(5, None or { 5 })
    @Test fun doesNotComputeDefaultWhenValue() = assertEquals(3, Value(3) or mootProvider)

    @Test fun replacesNoneWithDefaultNone() = assertNone(None orMaybe None)
    @Test fun replacesNoneWithDefaultValue() = assertValue(5, None orMaybe Value(5))
    @Test fun doesNotReplaceValueWithDefaultNone() = assertValue(3, Value(3) orMaybe None)
    @Test fun doesNotReplaceValueWithDefaultValue() = assertValue(3, Value(3) orMaybe Value(5))

    @Test fun replacesNoneWithComputedDefaultNone() = assertNone(None orMaybe { None })
    @Test fun replacesNoneWithComputedDefaultValue() = assertValue(5, None orMaybe { Value(5) })
    @Test fun doesNotComputeDefaultOptionalWhenValue() = assertValue(3, Value(3) orMaybe mootProvider)
}
