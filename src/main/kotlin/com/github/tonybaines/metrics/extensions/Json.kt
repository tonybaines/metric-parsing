package com.github.tonybaines.metrics.extensions

import com.github.tonybaines.metrics.MetricRecord

fun MetricRecord.GraphiteMetric.asJson(): String =
    """{ "timestamp":"${this.timestamp}", "id":{ "name":"${this.id}" }, "value":"${this.value.value}", "tags":${this.tags.asJson()} }"""

fun MetricRecord.CarbonMetric.asJson(): String =
    """{ "timestamp":"${this.timestamp}", "id":${this.intrinsicTags.asJson()} , "value":"${this.value.value}", "tags":${this.metaTags.asJson()} }"""


fun Tags.asJson() = """{ ${this.map { """ "${it.key}": "${it.value}" """ }.joinToString(",")} }"""
