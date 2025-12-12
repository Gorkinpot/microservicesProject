package com.example.rabbit

import com.example.Service.BookingService
import com.example.dto.request.BookingRequestFromRabbit
import dev.kourier.amqp.BuiltinExchangeType
import dev.kourier.amqp.channel.AMQPChannel
import dev.kourier.amqp.connection.AMQPConnection
import dev.kourier.amqp.connection.amqpConfig
import dev.kourier.amqp.connection.createAMQPConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

object RabbitSetup {
    lateinit var connection: AMQPConnection
    lateinit var consumerChannel: AMQPChannel

    val config = amqpConfig {
        server {
            host = "rabbitmq"
            port = 5672
            user = "guest"
            password = "guest"
        }
    }

    suspend fun init(coroutineScope: CoroutineScope) {
        connection = createAMQPConnection(coroutineScope, config)
        consumerChannel = connection.openChannel()

        declareBookingSetup()
    }

    suspend fun declareBookingSetup() {
        consumerChannel.exchangeDeclare(
            name = "bookingServiceExchange",
            type = BuiltinExchangeType.FANOUT,
            durable = true
        )

        consumerChannel.queueDeclare(
            name = "PlaceAnOrderShivilovAY-ikbo-07-22",
            durable = false,
            exclusive = false,
            autoDelete = true
        )

        consumerChannel.queueBind(
            queue = "PlaceAnOrderShivilovAY-ikbo-07-22",
            exchange = "bookingServiceExchange",
            routingKey = ""
        )
    }

    suspend fun startConsumer() {
        val consumer = consumerChannel.basicConsume(
            queue = "PlaceAnOrderShivilovAY-ikbo-07-22",
            noAck = true,
        )

        for (delivery in consumer) {
            val message = delivery.message.body.decodeToString()
            val bookingRequest = Json.decodeFromString<BookingRequestFromRabbit>(message)

            println(bookingRequest)

            BookingService.addBookingRequest(bookingRequest)
        }
    }

    suspend fun stopConnection() {
        connection.close()
        consumerChannel.close()
    }
}