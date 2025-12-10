package com.example

import com.example.database.connectDatabase
import com.example.rabbit.RabbitSetup
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    connectDatabase()

    /*environment.monitor.subscribe(ApplicationStarted) { app ->
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
    }*/

    configureRouting()
}
