package com.github.tonybaines.metrics

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.hasSize
import java.io.File
import java.time.Instant

class AcceptanceTest {

    @Test
    fun `can parse a file with a mix of valid and invalid records`() {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/raw-metrics-data.txt"))

        expectThat(parser.validRecords().toList())
            .hasSize(7)
    }

    @Test
    fun `can parse a file containing only valid basic graphite format records`() {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/basic-graphite-valid.txt"))

        expectThat(parser.validRecords().toList()).containsExactlyInAnyOrder(
            MetricRecord.GraphiteMetric(
                id = "some.metric.name",
                value = Value.LongValue(1234),
                timestamp = Instant.ofEpochSecond(1562763195),
                tags = mapOf()
            ),
            MetricRecord.GraphiteMetric(
                id = "another.metric.value.cpu%",
                value = Value.DoubleValue(1.23e-1),
                timestamp = Instant.ofEpochSecond(1562763195),
                tags = mapOf()
            )
        )
    }

    @Test
    fun `can parse a file containing only valid graphite format records with tags`() {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/graphite-valid-with-tags.txt"))

        expectThat(parser.validRecords().toList()).containsExactly(
            MetricRecord.GraphiteMetric(
                id = "some.metric.name",
                value = Value.LongValue(1234),
                timestamp = Instant.ofEpochSecond(1562763195),
                tags = mapOf("foo" to "bar")
            ),
            MetricRecord.GraphiteMetric(
                id = "another.metric.value.cpu%",
                value = Value.DoubleValue(1.23e-1),
                timestamp = Instant.ofEpochSecond(1562763195),
                tags = mapOf("baz" to "bang", "answer" to "42")
            )
        )
    }

    @Test
    fun `can parse a file containing only valid carbon 2_0 format records`() {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/carbon-format.txt"))

        expectThat(parser.validRecords().toList()).containsExactly(
            MetricRecord.CarbonMetric(
                intrinsicTags = mapOf("mtype" to "rate", "unit" to "Req/s", "site" to "mydomain", "host" to "web12"),
                metaTags = mapOf("agent" to "statsdaemon1"),
                value = Value.LongValue(234),
                timestamp = Instant.ofEpochSecond(1560852124)
            ),
            MetricRecord.CarbonMetric(
                intrinsicTags = mapOf("mtype" to "rate", "unit" to "Req/s", "site" to "mydomain", "host" to "web12"),
                metaTags = mapOf(),
                value = Value.DoubleValue(3.0e8),
                timestamp = Instant.ofEpochSecond(1560852124)
            ),
            MetricRecord.CarbonMetric(
                intrinsicTags = mapOf("mtype" to "rate", "unit" to "WtF/s"),
                value = Value.LongValue(10),
                timestamp = Instant.ofEpochSecond(1560852124)
            )
        )
    }


}