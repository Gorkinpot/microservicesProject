package com.example

import com.example.Service.UserService
import com.example.dto.request.RegisterRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientRoutingTest {

    private val mockUserService = mockk<UserService>(relaxed = true)

    @Test
    fun testRegisterRoute() = testApplication {
        application {
            clientRouting()
        }

        val request = RegisterRequest(
            email = "wgwbg",
            username = "ivan",
            password = "12345"
        )

        val jsonRequest = Json.encodeToString(request)

        val response = client.post("/api/user/register") {
            contentType(ContentType.Application.Json)
            setBody(jsonRequest)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            """{"message":"User registered successfully!"}""",
            response.bodyAsText()
        )
    }
}
