package com.tomuvak.optional

import com.tomuvak.optional.Optional.None
import com.tomuvak.optional.Optional.Value
import com.tomuvak.optional.test.assertNone
import com.tomuvak.optional.test.assertValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NullablesTest {
    @Test fun noneOrNullIsNull() = assertNull(None.orNull<Int>())
    @Test fun valueOrNullIsValue() = assertEquals(3, Value(3).orNull())
    @Test fun valueOfNullOrNullIsNull() = assertNull(Value(null).orNull<Int?>())

    @Test fun nullToOptionalIsNone() = assertNone(null.toOptional<Int>())
    @Test fun valueToOptionalIsValue() = assertValue(3, 3.toOptional())
}
