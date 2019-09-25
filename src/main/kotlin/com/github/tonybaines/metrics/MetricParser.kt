package com.github.tonybaines.metrics

import java.io.File
import java.time.Instant

private val String.isLong: Boolean
    get() = this.toLongOrNull() != null

sealed class MetricRecord() {
    companion object {
        fun from(fields: List<String>): MetricRecord =
            BasicGraphiteMetric(fields[0], Value.from(fields[1]), fields[2].toInstant())
    }
    data class BasicGraphiteMetric(val id: String, val value: Value, val timestamp: Instant): MetricRecord() {
        override fun toString(): String = "[$timestamp : '$id' = '$value']"
    }
}

private fun String.toInstant(): Instant  {
    val epochTime = this.toLong()
    return if (epochTime > 2000000000) {
        Instant.ofEpochMilli(epochTime)
    } else {
        Instant.ofEpochSecond(epochTime)
    }
}

sealed class Value {
    companion object {
        fun from(i: Long): Value = LongValue(i)
        fun from(f: String): Value = when {
            f.isLong -> LongValue(f.toLong())
            else -> FloatValue(f.toFloat())
        }
    }

    data class LongValue(val value: Long) : Value() {
        override fun toString(): String = value.toString()
    }
    data class FloatValue(val value: Float) : Value(){
        override fun toString(): String = value.toString()
    }
}

class MetricParser(private val input: List<String>) {
    companion object {
        fun readingFrom(input: File): MetricParser = MetricParser(input.readLines())
    }

    fun validRecords(): List<MetricRecord> = input
        .map{it.split(' ')}
        .map { MetricRecord.from(it) }

}
