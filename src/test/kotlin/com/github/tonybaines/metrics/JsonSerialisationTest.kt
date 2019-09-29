package com.github.tonybaines.metrics

import com.github.tonybaines.metrics.MetricRecord.CarbonMetric
import com.github.tonybaines.metrics.MetricRecord.GraphiteMetric
import com.github.tonybaines.metrics.Value.DoubleValue
import com.github.tonybaines.metrics.Value.LongValue
import com.github.tonybaines.metrics.extensions.asJson
import com.github.tonybaines.metrics.extensions.matchesJson
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import java.time.Instant

class JsonSerialisationTest {

    @Test
    fun `a simple graphite record can be serialised to valid JSON`() {
        expectThat(
            GraphiteMetric("foo", LongValue(42), Instant.EPOCH).asJson()
        ).matchesJson(
            """{ 
              "id": { "name": "foo"}, 
              "timestamp": "1970-01-01T00:00:00Z", 
              "value": "42", 
              "tags": {} 
            }"""
        )
    }

    @Test
    fun `a graphite record with tags can be serialised to valid JSON`() {
        val tags = mapOf("bar" to "baz")
        expectThat(
            GraphiteMetric("foo", DoubleValue(Double.NEGATIVE_INFINITY), Instant.EPOCH, tags).asJson()
        ).matchesJson(
            """{ 
              "id": { "name": "foo"}, 
              "timestamp": "1970-01-01T00:00:00Z", 
              "value": "-Infinity",
              "tags": { 
                "bar": "baz" 
              }
            }"""
        )
    }


    @Test
    fun `a carbon record can be serialised to valid JSON`() {
        val intrinsic = mapOf("mtype" to "counter", "unit" to "Red-Lorries")
        val meta = mapOf("road" to "A14")

        expectThat(
            CarbonMetric(intrinsic, meta, DoubleValue(Double.NaN), Instant.EPOCH).asJson()
        ).matchesJson(
            """{
              "id" : {
                "mtype" : "counter", 
                "unit" : "Red-Lorries"
              },
              "timestamp": "1970-01-01T00:00:00Z", 
              "value": "NaN",
              "tags": { 
                "road": "A14" 
              }
            }"""
        )
    }
}