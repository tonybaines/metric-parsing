package com.github.tonybaines.metrics.extensions

import org.skyscreamer.jsonassert.JSONAssert
import strikt.api.Assertion


fun Assertion.Builder<String>.matchesJson(expected: String): Assertion.Builder<String> =
    assert("matches JSON") { actual ->
        JSONAssert.assertEquals(expected, actual, true)
    }