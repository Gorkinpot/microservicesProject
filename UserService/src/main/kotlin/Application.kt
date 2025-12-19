package com.example

import com.example.Service.UserService
import com.example.database.connectDatabase
import com.example.rabbit.RabbitSetup
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = prometheusRegistry
    }

    install(ContentNegotiation) {
        json()
    }

    val userService = UserService
    connectDatabase()

    environment.monitor.subscribe(ApplicationStarted) { app ->
        launch {
            RabbitSetup.init(this)
        }
    }

    environment.monitor.subscribe(ApplicationStopped) { app ->
        launch {
            RabbitSetup.stopConnection()
        }
    }

    routing {
        get("/metrics") {
            call.respondText(prometheusRegistry.scrape())
        }
    }

    roomSelectedEventRouting()
    clientRouting(userService)
}