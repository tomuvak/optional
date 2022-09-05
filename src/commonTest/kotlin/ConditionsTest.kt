package com.tomuvak.optional

import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import com.tomuvak.testing.mootProvider
import com.tomuvak.testing.scriptedFunction
import com.tomuvak.testing.scriptedProvider
import kotlin.test.Test

class ConditionsTest {
    @Test fun thenYieldsNoneWhenFalse() = assertNone(false.then(3))
    @Test fun thenYieldsValueWhenTrue() = assertValue(3, true.then(3))

    @Test fun thenWithProviderWhenFalse() = assertNone(false.then(mootProvider))
    @Test fun thenWithProviderWhenTrue() = assertValue(3, true.then(scriptedProvider(3)))

    @Test fun ifSatisfiesWhenDoesNotSatisfy() = assertNone(3.ifSatisfies(scriptedFunction(3 to false)))
    @Test fun ifSatisfiesWhenSatisfies() = assertValue(3, 3.ifSatisfies(scriptedFunction(3 to true)))

    @Test fun ifIsWhenIsnt() = assertNone(3.ifIs<String>())
    @Test fun ifIsWhenNullAndIsnt() = assertNone(null.ifIs<Int>())
    @Test fun ifIsWhenIs() = assertValue(3, (3 as Any).ifIs<Int>())
    @Test fun ifIsWhenNullAndIs() = assertValue(null, (null as String?).ifIs<Int?>())
}
