package com.example

import com.example.database.connectDatabase
import com.example.rabbit.RabbitSetup
import io.ktor.server.application.*
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.cio.EngineMain.main(args)
}

fun Application.module() {
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

    roomSelectedEventRouting()
}