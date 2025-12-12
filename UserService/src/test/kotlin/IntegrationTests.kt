package com.example

import com.example.Service.UserService
import com.example.database.User
import com.example.database.initUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.*
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserServiceIntegrationTest {

    companion object {
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("testdb")
            withUsername("user")
            withPassword("pass")
            start() // контейнер стартует до подключения
        }
    }

    @BeforeAll
    fun setupDatabase() {
        Database.connect(
            url = postgresContainer.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgresContainer.username,
            password = postgresContainer.password
        )
        initUtils()
    }

    @BeforeEach
    fun cleanDatabase() {
        // Чистим таблицу перед каждым тестом
        transaction {
            User.deleteAll()
        }
    }

    @Test
    @Order(1)
    fun `register should insert user into db`() {
        val request = com.example.dto.request.RegisterRequest(
            email = "jane@example.com",
            username = "jane",
            password = "pass123"
        )
        UserService.register(request)

        transaction {
            val usersCount = User.selectAll().count()
            assertEquals(1, usersCount)
        }
    }

    @Test
    @Order(2)
    fun `authorize should return true for existing user`() {
        val registerRequest = com.example.dto.request.RegisterRequest(
            email = "john@example.com",
            username = "john",
            password = "pass456"
        )
        UserService.register(registerRequest)

        val authRequest = com.example.dto.request.AuthRequest(
            username = "john",
            password = "pass456"
        )
        val result = UserService.authorize(authRequest)

        assertEquals(true, result)
    }

    @AfterAll
    fun teardown() {
        postgresContainer.stop() // останавливаем контейнер после всех тестов
    }
}

