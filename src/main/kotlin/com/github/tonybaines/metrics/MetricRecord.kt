package com.github.tonybaines.metrics

import com.github.tonybaines.metrics.extensions.*
import io.vavr.control.Try
import io.vavr.kotlin.`try`
import java.time.Instant


sealed class MetricRecord {
    companion object {
        fun from(fields: List<String>): Try<out MetricRecord> =
            CarbonMetric.from(fields)
                .orElse { GraphiteMetric.from(fields) }

        private val VALID_METRIC_NAME_PATTERN = """[a-zA-Z]+[_+%\-\w.]+""".toRegex()
        private fun String.ensureValidMetricName(): String =
            if (this.matches(VALID_METRIC_NAME_PATTERN)) this
            else throw IllegalArgumentException("'$this' is not a valid value for a tag name")

    }

    data class CarbonMetric(
        val intrinsicTags: Tags,
        val extrinsicTags: Tags = mapOf(),
        val value: Value,
        val timestamp: Instant
    ) : MetricRecord() {
        companion object {
            fun from(fields: List<String>): Try<MetricRecord> =
                `try` {
                    if (fields.size < 3) throw java.lang.IllegalArgumentException("Not enough fields after splitting")
                    // Easier to work with in reverse, since the last two items are known
                    val reversed = fields.asReversed()
                    val tagList = reversed.drop(2)

                    CarbonMetric(
                        timestamp = reversed[0].toInstant(),
                        value = Value.from(reversed[1]),
                        intrinsicTags = tagList.intrinsicTags(),
                        extrinsicTags = tagList.extrinsicTags()
                    )
                }
        }
    }

    data class GraphiteMetric(
        val id: String,
        val value: Value,
        val timestamp: Instant,
        val tags: Tags = mapOf()
    ) : MetricRecord() {
        companion object {
            fun from(fields: List<String>): Try<MetricRecord> =
                `try` {
                    GraphiteMetric(
                        id = fields[0].withoutTags().ensureValidMetricName(),
                        value = Value.from(fields[1]),
                        timestamp = fields[2].toInstant(),
                        tags = fields[0].extractTags()
                    )
                }

            private fun String.withoutTags(): String = this.takeWhile { it != ';' }

            private val GRAPHITE_TAG_VALUE_PATTERN = """[_+%\-\w]+""".toRegex()
            private fun String.extractTags(): Tags = this
                .split(';')
                .asTags(tagValuePattern = GRAPHITE_TAG_VALUE_PATTERN)
        }
    }
}

sealed class Value {
    abstract val value: Number

    companion object {
        fun from(s: String): Value = when {
            s.isLong -> LongValue(s.toLong())
            s.matches("""nan""".toRegex(RegexOption.IGNORE_CASE)) -> DoubleValue(Double.NaN)
            s.matches("""-inf(inity)?""".toRegex(RegexOption.IGNORE_CASE)) -> DoubleValue(Double.NEGATIVE_INFINITY)
            s.matches("""[+]?inf(inity)?""".toRegex(RegexOption.IGNORE_CASE)) -> DoubleValue(Double.POSITIVE_INFINITY)
            else -> DoubleValue(s.toDouble())
        }

    }

    data class LongValue(override val value: Long) : Value()
    data class DoubleValue(override val value: Double) : Value()
}