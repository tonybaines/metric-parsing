package com.github.tonybaines.metrics.extensions

import java.time.Instant

fun String.toInstant(): Instant {
    val epochTime = this.toLong()
    // Timestamps may be seconds or milliseconds since the epoch
    return if (epochTime > Instant.now().epochSecond * 10) {
        Instant.ofEpochMilli(epochTime)
    } else {
        Instant.ofEpochSecond(epochTime)
    }
}

val String.isLong: Boolean
    get() = this.toLongOrNull() != null