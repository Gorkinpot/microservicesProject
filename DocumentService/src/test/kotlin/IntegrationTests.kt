import com.example.Service.DocumentRepository
import com.example.database.User
import com.example.database.UserDocument
import com.example.dto.request.DocumentAddingRequest
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

class DocumentServiceIntegrationTest {

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
            SchemaUtils.create(User, UserDocument)
        }

        // Создадим пользователя для внешнего ключа
        transaction {
            User.insert {
                it[email] = "john@example.com"
                it[username] = "john"
                it[password] = "pass"
            }
        }
    }

    @AfterEach
    fun teardown() {
        transaction {
            UserDocument.deleteAll()
            User.deleteAll()
        }
    }

    @Test
    fun `addDocument should insert document into DB`() {
        val userId = transaction { User.selectAll().first()[User.id] }
        val request = DocumentAddingRequest(userId = userId, document = "passport.pdf")

        DocumentRepository.addDocument(request)

        transaction {
            val count = UserDocument.selectAll().count()
            assertEquals(1, count)
        }
    }
}
