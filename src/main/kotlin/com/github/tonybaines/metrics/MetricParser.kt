package com.github.tonybaines.metrics

import java.io.File

class MetricParser(private val input: Sequence<String>) {
    companion object {
        fun readingFrom(input: File): MetricParser = MetricParser(input.readLines().asSequence())
    }

    fun validRecords(): Sequence<MetricRecord> = input
        .map { line -> MetricRecord.from(line) }
//        .also { attempts -> attempts.filter { it.isFailure }.onEach { println(it.cause) } }
        .filter { attempt -> attempt.isSuccess }
        .map { successful -> successful.get() }

}
