import com.example.Service.BookingRepository
import com.example.database.Booking
import com.example.database.Room
import com.example.database.User
import com.example.dto.request.BookingRequestFromRabbit
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals

class BookingServiceIntegrationTest {

    companion object {
        val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("testdb")
            withUsername("user")
            withPassword("pass")
            start()
        }
    }

    @BeforeEach
    fun setup() {
        Database.connect(
            url = postgres.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgres.username,
            password = postgres.password
        )

        transaction {
            SchemaUtils.create(User, Room, Booking)
        }

        transaction {
            User.insert {
                it[email] = "john@example.com"
                it[username] = "john"
                it[password] = "pass"
            }
            Room.insert {
                it[name] = "Room A"
                it[address] = "123 Street"
                it[price] = 100.0
            }
        }
    }

    @AfterEach
    fun teardown() {
        transaction {
            Booking.deleteAll()
            Room.deleteAll()
            User.deleteAll()
        }
    }

    @Test
    fun `addBookingInfo should insert booking into DB`() {
        val userId = transaction { User.selectAll().first()[User.id] }
        val roomId = transaction { Room.selectAll().first()[Room.id] }

        val request = BookingRequestFromRabbit(userId = userId, roomId = roomId, document = "passport.pdf")
        BookingRepository.addBookingInfo(request)

        transaction {
            val count = Booking.selectAll().count()
            assertEquals(1, count)
        }
    }

    @Test
    fun `deleteBookingInfo should remove booking from DB`() {
        val userId = transaction { User.selectAll().first()[User.id] }
        val roomId = transaction { Room.selectAll().first()[Room.id] }

        val request = BookingRequestFromRabbit(userId = userId, roomId = roomId, document = "passport.pdf")
        BookingRepository.addBookingInfo(request)

        val cancelRequest = com.example.dto.request.BookingCancelRequest(userId = userId)
        BookingRepository.deleteBookingInfo(cancelRequest)

        transaction {
            val count = Booking.selectAll().count()
            assertEquals(0, count)
        }
    }
}
