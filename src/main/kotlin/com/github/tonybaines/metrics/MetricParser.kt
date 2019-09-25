package com.github.tonybaines.metrics

import java.io.File

data class MetricRecord(val id: String, val value: Any)

class MetricParser(input: List<String>) {
    companion object {
        fun readingFrom(input: File): MetricParser = MetricParser(input.readLines())
    }

    fun validRecords(): List<out MetricRecord> = emptyList()


}
