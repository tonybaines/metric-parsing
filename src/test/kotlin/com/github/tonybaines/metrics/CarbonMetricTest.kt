package com.github.tonybaines.metrics

import com.github.tonybaines.metrics.extensions.Tags
import org.junit.jupiter.api.Test
import strikt.api.expectThrows
import java.time.Instant

internal class CarbonMetricTest {
    @Test
    fun `must have a complete set of valid intrinsic tags`() {
        expectThrows<IllegalArgumentException> { carbonMetric(mapOf("unit" to "cm")) }
        expectThrows<IllegalArgumentException> { carbonMetric(mapOf("mtype" to "height")) }
    }

    private fun carbonMetric(intrinsicTags: Tags): MetricRecord.CarbonMetric {
        return MetricRecord.CarbonMetric(
            intrinsicTags = intrinsicTags,
            value = Value.LongValue(42),
            timestamp = Instant.now()
        )
    }
}