package com.github.tonybaines.metrics

import io.vavr.control.Try
import io.vavr.kotlin.`try`
import java.io.File
import java.time.Instant

sealed class MetricRecord() {
    companion object {
        fun from(fields: List<String>): Try<out MetricRecord> =
            GraphiteMetric.from(fields)
    }

    data class GraphiteMetric(
        val id: String,
        val value: Value,
        val timestamp: Instant,
        val tags: Map<String, String> = mapOf()
    ) : MetricRecord() {
        companion object {
            fun from(fields: List<String>): Try<GraphiteMetric> =
                `try` { GraphiteMetric(fields[0], Value.from(fields[1]), fields[2].toInstant(), mapOf()) }
        }
    }

}

private fun String.toInstant(): Instant {
    val epochTime = this.toLong()
    return if (epochTime > 2000000000) {
        Instant.ofEpochMilli(epochTime)
    } else {
        Instant.ofEpochSecond(epochTime)
    }
}

sealed class Value {
    companion object {
        fun from(l: Long): Value = LongValue(l)
        fun from(s: String): Value = when {
            s.isLong -> LongValue(s.toLong())
            else -> DoubleValue(s.toDouble())
        }

        private val String.isLong: Boolean
            get() = this.toLongOrNull() != null
    }

    data class LongValue(val value: Long) : Value()
    data class DoubleValue(val value: Double) : Value()
}

class MetricParser(private val input: List<String>) {
    companion object {
        fun readingFrom(input: File): MetricParser = MetricParser(input.readLines())
    }

    fun validRecords(): List<MetricRecord> = input
        .map { it.split(' ') }
        .map { fields -> MetricRecord.from(fields) }
        .filter { attempt -> attempt.isSuccess }
        .map { successful -> successful.get() }

}
