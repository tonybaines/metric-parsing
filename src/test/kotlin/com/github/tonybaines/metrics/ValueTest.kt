package com.github.tonybaines.metrics

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class ValueTest {
    @Test
    fun `valid double values include infinities and NaN`() {
        expectThat(Value.from("NaN").value).isEqualTo(Double.NaN)
        expectThat(Value.from("nan").value).isEqualTo(Double.NaN)
        expectThat(Value.from("+inf").value).isEqualTo(Double.POSITIVE_INFINITY)
        expectThat(Value.from("inf").value).isEqualTo(Double.POSITIVE_INFINITY)
        expectThat(Value.from("+infinity").value).isEqualTo(Double.POSITIVE_INFINITY)
        expectThat(Value.from("infinity").value).isEqualTo(Double.POSITIVE_INFINITY)
        expectThat(Value.from("-inf").value).isEqualTo(Double.NEGATIVE_INFINITY)
        expectThat(Value.from("-infinity").value).isEqualTo(Double.NEGATIVE_INFINITY)
    }
}