package com.example

import com.example.dto.request.OrderPlacingRequest
import com.example.dto.response.OrderPlacingResponse
import com.example.service.CartService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    routing {
        get("/api/cart/placeAnOrder") {
            val request = call.receive<OrderPlacingRequest>()

            val json = Json.encodeToString(request)
            val message = json.toByteArray()

            CartService.publishToRabbit(message)

            call.respond(HttpStatusCode.OK, OrderPlacingResponse("Booking is initiated!"))
        }
    }
}
