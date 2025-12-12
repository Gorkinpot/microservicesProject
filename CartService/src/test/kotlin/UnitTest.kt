import com.example.rabbit.RabbitSetup
import com.example.service.CartService
import io.mockk.coVerify
import io.mockk.mockkObject
import io.mockk.every
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class CartServiceUnitTest {

    @Test
    fun `publishToRabbit should call basicPublish`() = runBlocking {
        mockkObject(RabbitSetup)

        val message = "Test message".toByteArray()

        // Мокаем канал
        val mockChannel = io.mockk.mockk<dev.kourier.amqp.channel.AMQPChannel>(relaxed = true)
        every { RabbitSetup.documentServiceProducerChannel } returns mockChannel

        CartService.publishToRabbit(message)

        coVerify { mockChannel.basicPublish(body = message, exchange = "documentServiceExchange", routingKey = "documents.documentCheck") }
    }
}
