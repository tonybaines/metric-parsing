package com.github.tonybaines.metrics

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.specs.StringSpec
import java.io.File

class AcceptanceTest : StringSpec({

    "can parse a file with a mix of valid and invalid records" {
        val parser: MetricParser = MetricParser.readingFrom(File("src/test/resources/raw-metrics-data.txt"))

        parser.validRecords().shouldHaveSize(6)
    }
})