package com.example

import com.example.database.connectDatabase
import com.example.rabbit.RabbitSetup
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Application.module() {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = prometheusRegistry
    }

    install(ContentNegotiation) {
        json()
    }

    connectDatabase()

    environment.monitor.subscribe(ApplicationStarted) { app ->
        launch {
            RabbitSetup.init(this)

            launch(Dispatchers.IO) {
                RabbitSetup.startConsumer()
            }
        }
    }

    environment.monitor.subscribe(ApplicationStopped) { app ->
        launch {
            RabbitSetup.stopConnection()
        }
    }

    configureRouting()
    
    routing {
        get("/metrics") {
            call.respondText(prometheusRegistry.scrape())
        }
    }
}
