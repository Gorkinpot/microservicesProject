package com.example

import com.example.Service.BookingService
import com.example.dto.request.BookingCancelRequest
import com.example.dto.response.BookingCancelResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        delete("/api/booking/cancelBooking") {
            val request = call.receive<BookingCancelRequest>()
            BookingService.deleteBookingRequest(request)
            call.respond(HttpStatusCode.OK, BookingCancelResponse("Booking info has been deleted"))
        }
    }
}
