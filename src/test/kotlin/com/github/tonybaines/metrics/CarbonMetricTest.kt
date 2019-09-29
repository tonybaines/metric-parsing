package com.github.tonybaines.metrics

import com.github.tonybaines.metrics.MetricRecord.CarbonMetric
import com.github.tonybaines.metrics.Value.LongValue
import com.github.tonybaines.metrics.extensions.Tags
import io.vavr.control.Validation
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.time.Instant

internal class CarbonMetricTest {
    @Test
    fun `can parse a valid Carbon record with no meta tags from a line of text`() {
        expectThat(CarbonMetric.from("site=mydomain mtype=rate unit=Req/s 234 1234567890").get())
            .isEqualTo(
                CarbonMetric(
                    intrinsicTags = mapOf("site" to "mydomain", "mtype" to "rate", "unit" to "Req/s"),
                    timestamp = Instant.ofEpochSecond(1234567890),
                    value = LongValue(234)
                )
            )
    }

    @Test
    fun `can parse a valid Carbon record with meta tags from a line of text`() {
        expectThat(CarbonMetric.from("site=mydomain mtype=rate unit=Req/s  agent=foo 234 1234567890").get())
            .isEqualTo(
                CarbonMetric(
                    intrinsicTags = mapOf("site" to "mydomain", "mtype" to "rate", "unit" to "Req/s"),
                    metaTags = mapOf("agent" to "foo"),
                    timestamp = Instant.ofEpochSecond(1234567890),
                    value = LongValue(234)
                )
            )
    }

    @Test
    fun `fails to parse a line with an invalid tag value`() {
        expectThat(MetricRecord.from("site=mydom|ain mtype=rate host=web12  agent=statsdaemon1 234 1560852124"))
            .isA<Validation.Invalid<Failure, MetricRecord>>()
    }

    @Test
    fun `must have a complete set of valid intrinsic tags`() {
        expectThrows<IllegalArgumentException> { carbonMetric(mapOf("unit" to "cm")) }
        expectThrows<IllegalArgumentException> { carbonMetric(mapOf("mtype" to "height")) }
    }

    private fun carbonMetric(intrinsicTags: Tags): CarbonMetric {
        return CarbonMetric(
            intrinsicTags = intrinsicTags,
            value = LongValue(42),
            timestamp = Instant.now()
        )
    }
}