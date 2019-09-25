package com.github.tonybaines.metrics

import java.io.File
import java.time.Instant

sealed class MetricRecord(val id: String, val value: Value, val timestamp: Instant) {
    class BasicGraphiteMetric(id: String, value: Value, timestamp: Instant): MetricRecord(id, value, timestamp) {
        override fun toString(): String = "[$timestamp : '$id' = '$value']"
    }
}

sealed class Value {
    companion object {
        fun from(i: Int): Value = IntegerValue(i)
        fun from(f: String): Value = FloatValue(f.toFloat())
    }

    data class IntegerValue(val value: Int) : Value() {
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

    fun validRecords(): List<MetricRecord> = emptyList()


}
