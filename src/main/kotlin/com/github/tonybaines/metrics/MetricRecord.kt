package com.github.tonybaines.metrics

import com.github.tonybaines.metrics.extensions.*
import io.vavr.control.Try
import io.vavr.kotlin.`try`
import java.time.Instant


sealed class MetricRecord {
    companion object {
        fun from(line: String): Try<out MetricRecord> =
            CarbonMetric.from(line)
                .orElse { GraphiteMetric.from(line) }

        private val VALID_METRIC_NAME_PATTERN = """[a-zA-Z]+[_+%\-\w.]+""".toRegex()
        private fun String.ensureValidMetricName(): String =
            if (this.matches(VALID_METRIC_NAME_PATTERN)) this
            else throw IllegalArgumentException("'$this' is not a valid value for a tag name")

    }

    data class CarbonMetric(
        val intrinsicTags: Tags,
        val metaTags: Tags = mapOf(),
        val value: Value,
        val timestamp: Instant
    ) : MetricRecord() {
        init {
            // Validation
            intrinsicTags.ensureComplete()
            intrinsicTags.validateTags(CARBON_TAG_VALUE_PATTERN)
            metaTags.validateTags(CARBON_TAG_VALUE_PATTERN)
        }
        companion object {
            fun from(line: String): Try<MetricRecord> =
                `try` {
                    val startOfMetaTags = line.indexOf("  ")
                    val hasMetaTags = (startOfMetaTags > 0) && (startOfMetaTags < line.length)

                    if (hasMetaTags) {
                        val intrinsicTags = line.substring(0..startOfMetaTags).split(' ')
                        val metaAndRest = line.substring(startOfMetaTags).split(' ').reversed()
                        val metaTags = metaAndRest.drop(2)
                        CarbonMetric(
                            timestamp = metaAndRest[0].toInstant(),
                            value = Value.from(metaAndRest[1]),
                            intrinsicTags = intrinsicTags.asTags(),
                            metaTags = metaTags.asTags()
                        )
                    } else {
                        val intrinsicAndRest = line.split(' ').reversed()
                        val intrinsicTags = intrinsicAndRest.drop(2)
                        CarbonMetric(
                            timestamp = intrinsicAndRest[0].toInstant(),
                            value = Value.from(intrinsicAndRest[1]),
                            intrinsicTags = intrinsicTags.asTags(),
                            metaTags = mapOf()
                        )
                    }
                }

            private val CARBON_TAG_VALUE_PATTERN = """[_+%\-/\w]+""".toRegex()
        }
    }

    data class GraphiteMetric(
        val id: String,
        val value: Value,
        val timestamp: Instant,
        val tags: Tags = mapOf()
    ) : MetricRecord() {
        init {
            // Validation
            id.ensureValidMetricName()
            tags.validateTags(valuePattern = GRAPHITE_TAG_VALUE_PATTERN)
        }
        companion object {
            fun from(line: String): Try<MetricRecord> =
                `try` {
                    val fields = line.split(' ')
                    GraphiteMetric(
                        id = fields[0].withoutTags(),
                        value = Value.from(fields[1]),
                        timestamp = fields[2].toInstant(),
                        tags = fields[0].extractTags()
                    )
                }

            private fun String.withoutTags(): String = this.takeWhile { it != ';' }

            private val GRAPHITE_TAG_VALUE_PATTERN = """[_+%\-\w]+""".toRegex()
            private fun String.extractTags(): Tags = this
                .split(';')
                .asTags()
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