package com.github.tonybaines.metrics

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        MetricParser(generateSequence(::readLine)).validRecords().map { it.asJson() }.forEach(::println)
    }
}