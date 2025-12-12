import com.example.Service.UserService
import com.example.clientRouting
import com.example.dto.request.RegisterRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class ClientRoutingTest {

    private val mockUserService = mockk<UserService>(relaxed = true)

    @Test
    fun testRegisterRoute() = testApplication {

        every { mockUserService.register(any()) } returns Unit

        application {
            clientRouting(mockUserService)
        }

        val request = RegisterRequest(
            email = "wgwbg",
            username = "ivan",
            password = "12345"
        )

        val response = client.post("/api/user/register") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(request))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(
            """{"message":"User registered successfully!"}""",
            response.bodyAsText()
        )

        verify { mockUserService.register(any()) }
    }
}
