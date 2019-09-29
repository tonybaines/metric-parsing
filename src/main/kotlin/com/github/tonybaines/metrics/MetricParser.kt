package com.github.tonybaines.metrics

import java.io.File

data class Failure(val line: String, val cause: Throwable) {
    override fun toString(): String = """The line '$line' is invalid: ${cause}"""
}

class MetricParser(private val input: Sequence<String>) {
    companion object {
        fun readingFrom(input: File): MetricParser = MetricParser(input.readLines().asSequence())
    }

    fun validRecords(): Sequence<MetricRecord> = input
        .filter(String::isNotBlank)
        .map(String::trim)
        .map { line -> MetricRecord.from(line) }
        .onEach { if (it.isInvalid) System.err.println(it.error) }
        .filter { attempt -> attempt.isValid }
        .map { successful -> successful.get() }

}
