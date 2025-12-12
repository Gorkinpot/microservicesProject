import com.example.Service.BookingRepository
import com.example.Service.BookingService
import com.example.dto.request.BookingCancelRequest
import com.example.dto.request.BookingRequestFromRabbit
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlin.test.Test

class BookingServiceUnitTest {

    @Test
    fun `addBookingRequest should call repository`() {
        mockkObject(BookingRepository)

        val request = BookingRequestFromRabbit(userId = 1, roomId = 2, document = "passport.pdf")
        every { BookingRepository.addBookingInfo(request) } returns Unit

        BookingService.addBookingRequest(request)

        verify { BookingRepository.addBookingInfo(request) }
    }

    @Test
    fun `deleteBookingRequest should call repository`() {
        mockkObject(BookingRepository)

        val request = BookingCancelRequest(userId = 1)
        every { BookingRepository.deleteBookingInfo(request) } returns Unit

        BookingService.deleteBookingRequest(request)

        verify { BookingRepository.deleteBookingInfo(request) }
    }
}
