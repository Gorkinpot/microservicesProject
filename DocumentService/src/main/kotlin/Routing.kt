package com.example

import com.example.Service.DocumentService
import com.example.dto.request.DocumentAddingRequest
import com.example.dto.response.DocumentAddingResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        post("/api/document/addDocument") {
            val request = call.receive<DocumentAddingRequest>()
            DocumentService.addDocument(request)
            call.respond(HttpStatusCode.OK, DocumentAddingResponse("Document has been successfully added"))
        }
    }
}
