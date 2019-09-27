package com.github.tonybaines.metrics

import com.github.tonybaines.metrics.extensions.Tags
import org.junit.jupiter.api.Test
import strikt.api.expectThrows
import java.time.Instant

internal class GraphiteMetricTest {
    @Test
    fun `must have valid metric identifiers`() {
        expectThrows<IllegalArgumentException> { graphiteMetric("foo!") }
        expectThrows<IllegalArgumentException> { graphiteMetric("foo=") }
        expectThrows<IllegalArgumentException> { graphiteMetric("0foo") }
    }

    @Test
    fun `must have valid metric names and values`() {
        expectThrows<java.lang.IllegalArgumentException> { graphiteMetric("foo", mapOf("bar" to "")) }
        expectThrows<java.lang.IllegalArgumentException> { graphiteMetric("foo", mapOf("" to "bar")) }
    }

    private fun graphiteMetric(id: String, tags: Tags = mapOf()): MetricRecord.GraphiteMetric {
        return MetricRecord.GraphiteMetric(
            id = id,
            value = Value.LongValue(42),
            timestamp = Instant.now(),
            tags = tags
        )
    }
}