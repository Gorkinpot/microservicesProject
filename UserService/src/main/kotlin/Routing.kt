package com.example

import com.example.Service.UserRepository
import com.example.Service.UserService
import com.example.dto.request.AuthRequest
import com.example.dto.request.RegisterRequest
import com.example.dto.request.CartItemRequestToRabbit
import com.example.dto.response.AuthResponse
import com.example.dto.response.RegisterResponse
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
import java.util.UUID
import kotlin.uuid.Uuid

fun Application.roomSelectedEventRouting() {
    routing {
        get("/api/user/selectRoom") {
            val request = call.receive<CartItemRequestToRabbit>()
            val json = Json.encodeToString(request)
            val message = json.toByteArray()

            UserService.publishToRabbit(message)

            call.respondText("Room added to cart!")
        }
    }
}

fun Application.clientRouting(userService: UserService) {
    routing {
        post("/api/user/register") {
            try {
                val request = call.receive<RegisterRequest>()
                userService.register(request)
                println("Usre has been registered")
                call.respond(HttpStatusCode.Created, RegisterResponse("User registered successfully!"))
            } catch (e : Exception) {
                println(e.message)
            }
        }

        get("/api/user/auth") {
            try {
                val request = call.receive<AuthRequest>()
                val result = AuthResponse(token = UUID.randomUUID().toString())
                if (userService.authorize(request) == true) {
                    call.respond(HttpStatusCode.OK, "User already exists! ${result}")
                    println("Usre has been authorized")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User has not authorized!")
                }
            } catch (e : Exception) {
                println(e.message)
            }
        }
    }
}
