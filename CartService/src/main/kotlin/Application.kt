package com.example

import com.example.database.connectDatabase
import com.example.rabbit.RabbitSetup
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Application.module() {
    connectDatabase()
    configureRouting()

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
}

