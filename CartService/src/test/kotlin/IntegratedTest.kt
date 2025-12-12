import com.example.database.CartItem
import com.example.database.Room
import com.example.database.User
import com.example.database.insertToCart
import com.example.dto.request.CartItemRequestFromRabbit
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import kotlin.test.assertEquals

class CartServiceIntegrationTest {

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
        // Подключаемся к Testcontainers базе
        Database.connect(
            url = postgres.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgres.username,
            password = postgres.password
        )

        // Создаем таблицы
        transaction {
            SchemaUtils.create(User, Room, CartItem)
        }
    }

    @AfterEach
    fun teardown() {
        // Очищаем таблицы после теста
        transaction {
            CartItem.deleteAll()
            Room.deleteAll()
            User.deleteAll()
        }
    }

    @Test
    fun `insertToCart should insert CartItem into DB`() {
        // Вставляем пользователя и комнату, чтобы внешние ключи не падали
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

        // Получаем id вставленных записей
        val userId = transaction { User.selectAll().first()[User.id] }
        val roomId = transaction { Room.selectAll().first()[Room.id] }

        val request = CartItemRequestFromRabbit(userId = userId, roomId = roomId)
        insertToCart(request)

        transaction {
            val count = CartItem.selectAll().count()
            assertEquals(1, count)
        }
    }
}
