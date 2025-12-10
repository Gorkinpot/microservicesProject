package com.example

import com.example.Service.UserRepository
import com.example.Service.UserService
import com.example.dto.request.RegisterRequest
import com.example.dto.request.CartItemRequest
import com.example.rabbit.RabbitSetup
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json

fun Application.roomSelectedEventRouting() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/api/user/selectRoom") {
            val request = call.receive<CartItemRequest>()
            val json = Json.encodeToString(request)
            val message = json.toByteArray()

            val channel = RabbitSetup.channel
            channel.basicPublish(
                body = message,
                exchange = "RoomSelectedExchange",
                routingKey = ""
            )

            println("Message sent!")

            call.respondText("Message from server sent!")
        }

        post("/api/user/register") {
            try {
                val request = call.receive<RegisterRequest>()
                UserService(UserRepository).register(request)
                call.respond(HttpStatusCode.Created)
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}

fun Application.clientRouting(){

}
