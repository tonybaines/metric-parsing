package com.github.tonybaines.metrics

import java.io.File

class MetricParser(private val input: List<String>) {
    companion object {
        fun readingFrom(input: File): MetricParser = MetricParser(input.readLines())
    }

    fun validRecords(): List<MetricRecord> = input
        .map { it.split(' ') }
        .map { fields -> MetricRecord.from(fields) }
//        .also { attempts -> attempts.filter { it.isFailure }.onEach { println(it.cause) } }
        .filter { attempt -> attempt.isSuccess }
        .map { successful -> successful.get() }

}
