package com.github.tonybaines.metrics

import io.vavr.control.Validation
import io.vertx.core.AbstractVerticle


@Suppress("unused")
class HttpServer : AbstractVerticle() {
    override fun start() {
        vertx.createHttpServer()
            .requestHandler { req ->
                req.bodyHandler { event ->
                    val record = event.toJsonObject().getString("record")
                    if (record == null) {
                        req.response().setStatusCode(400).end("Form parameter 'record' required.")
                    } else {
                        val result: Validation<Failure, out MetricRecord> = MetricRecord.from(record)

                        when (result) {
                            is Validation.Valid<*, *> -> req.response()
                                .putHeader("content-type", "application/json")
                                .end(result.get().asJson())

                            else -> req.response()
                                .setStatusCode(500)
                                .end(result.error.toString())
                        }
                    }
                }

            }.listen(8080)
    }
}