import com.example.Service.UserRepository
import com.example.Service.UserService
import com.example.dto.request.AuthRequest
import com.example.dto.request.RegisterRequest
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertTrue

class UserServiceUnitTest {

    @Test
    fun `register should call repository`() {
        mockkObject(UserRepository)

        val request = RegisterRequest("john@example.com", "john", "pass")
        every { UserRepository.registerUser(request) } returns Unit

        UserService.register(request)

        verify { UserRepository.registerUser(request) }
    }

    @Test
    fun `authorize should return true if user exists`() {
        mockkObject(UserRepository)

        val request = AuthRequest("john", "pass")
        every { UserRepository.authorizeUser(request) } returns true

        val result = UserService.authorize(request)

        assertTrue(result)
        verify { UserRepository.authorizeUser(request) }
    }
}
