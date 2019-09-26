package com.github.tonybaines.metrics

import io.kotlintest.matchers.collections.shouldContainInOrder
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.specs.StringSpec
import java.io.File
import java.time.Instant

class AcceptanceTest : StringSpec({

    "can parse a file with a mix of valid and invalid records" {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/raw-metrics-data.txt"))

        parser.validRecords().shouldHaveSize(6)
    }

    "can parse a file containing only valid basic graphite format records"() {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/basic-graphite-valid.txt"))

        parser.validRecords().shouldContainInOrder(
            MetricRecord.GraphiteMetric(
                "some.metric.name",
                Value.from(1234),
                Instant.ofEpochSecond(1562763195),
                mapOf()
            ),
            MetricRecord.GraphiteMetric(
                "another.metric.value.cpu%",
                Value.from("1.23e-1"),
                Instant.ofEpochSecond(1562763195),
                mapOf()
            )
        )
    }

    "can parse a file containing only valid graphite format records with tags"() {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/basic-graphite-valid.txt"))

        parser.validRecords().shouldContainInOrder(
            MetricRecord.GraphiteMetric(
                "some.metric.name",
                Value.from(1234),
                Instant.ofEpochSecond(1562763195),
                mapOf("foo" to "bar")
            ),
            MetricRecord.GraphiteMetric(
                "another.metric.value.cpu%",
                Value.from("1.23e-1"),
                Instant.ofEpochSecond(1562763195),
                mapOf("baz" to "bang", "answer" to "42")
            )
        )
    }
})