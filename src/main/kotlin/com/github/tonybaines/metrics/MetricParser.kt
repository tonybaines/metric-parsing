package com.github.tonybaines.metrics

import io.vavr.control.Try
import io.vavr.kotlin.`try`
import java.io.File
import java.lang.IllegalStateException
import java.time.Instant
import javax.xml.transform.OutputKeys

typealias Tags = Map<String, String>

sealed class MetricRecord {
    companion object {
        fun from(fields: List<String>): Try<out MetricRecord> =
            CarbonMetric.from(fields)
                .orElse { GraphiteMetric.from(fields) }

        private fun String.toInstant(): Instant {
            val epochTime = this.toLong()
            // Timestamps may be seconds or milliseconds since the epoch
            return if (epochTime > Instant.now().epochSecond * 10) {
                Instant.ofEpochMilli(epochTime)
            } else {
                Instant.ofEpochSecond(epochTime)
            }
        }
    }

    data class CarbonMetric(
        val intrinsicTags: Tags,
        val extrinsicTags: Tags = mapOf(),
        val value: Value,
        val timestamp: Instant
    ) : MetricRecord() {
        companion object {

            val INTRINSIC_TAGS = setOf("unit", "mtype")
            fun from(fields: List<String>): Try<MetricRecord> =
                `try` {
                    // Easier to work with in reverse, since the last two items are known
                    val reveresed = fields.asReversed()

                    CarbonMetric(
                        timestamp = reveresed[0].toInstant(),
                        value = Value.from(reveresed[1]),
                        intrinsicTags = reveresed.drop(2).intrinsicTags().validateContains(INTRINSIC_TAGS),
                        extrinsicTags = reveresed.drop(2).extrinsicTags()
                    )
                }

            val isIntrinsic: (String) -> Boolean = { key -> INTRINSIC_TAGS.contains(key) }

            private fun List<String>.intrinsicTags(): Tags =
                this.asMap().filterKeys(isIntrinsic)

            private fun List<String>.extrinsicTags(): Tags = this.asMap().filterKeys { !isIntrinsic(it) }

            private fun List<String>.asMap(): Tags = this
                .filter { tagPairs -> tagPairs.contains('=') }
                .map { tagPair -> tagPair.split('=') }
                .associate { pair -> pair[0] to pair[1] }
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
                        id = fields[0].withoutTags(),
                        value = Value.from(fields[1]),
                        timestamp = fields[2].toInstant(),
                        tags = fields[0].extractTags()
                    )
                }

            private fun String.withoutTags(): String = this.takeWhile { it != ';' }

            private fun String.extractTags(): Tags = this
                .split(';')
                .filter { tagPairs -> tagPairs.contains('=') }
                .map { tagPair -> tagPair.split('=') }
                .associate { pair -> pair[0] to pair[1] }
        }
    }

}

private fun Tags.validateContains(requiredKeys: Set<String>): Tags = if (this.keys == requiredKeys) this
else throw IllegalStateException("Required keys $requiredKeys not found in ${this.keys}")

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
