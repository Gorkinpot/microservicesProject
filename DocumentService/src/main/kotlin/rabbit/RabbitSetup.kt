package com.example.rabbit

import com.example.Service.DocumentRepository
import com.example.database.UserDocument
import com.example.dto.request.BookingRequestToRabbit
import com.example.dto.request.CartItem
import com.example.dto.response.DocumentExistenceResponse
import dev.kourier.amqp.BuiltinExchangeType
import dev.kourier.amqp.channel.AMQPChannel
import dev.kourier.amqp.connection.AMQPConnection
import dev.kourier.amqp.connection.amqpConfig
import dev.kourier.amqp.connection.createAMQPConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object RabbitSetup {
    lateinit var connection: AMQPConnection
    lateinit var consumerChannel: AMQPChannel
    lateinit var producerChannelToCartService: AMQPChannel
    lateinit var producerChannelToBookingService: AMQPChannel

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
        producerChannelToCartService = connection.openChannel()
        producerChannelToBookingService = connection.openChannel()

        declareCartSetup()
        declareBookingSetup()
    }

    suspend fun declareCartSetup() {
        consumerChannel.exchangeDeclare(
            name = "documentServiceExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )

        consumerChannel.queueDeclare(
            name = "PlaceAnOrderKomandinAY-ikbo-07-22",
            durable = true,
            exclusive = false,
            autoDelete = false
        )

        consumerChannel.queueBind(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            exchange = "documentServiceExchange",
            routingKey = "documents.documentCheck"
        )

        producerChannelToCartService.queueDeclare(
            name = "PlaceAnOrderKuklinMA-ikbo-07-22",
            durable = true,
            exclusive = false,
            autoDelete = false
        )

        producerChannelToCartService.queueBind(
            queue = "PlaceAnOrderKuklinMA-ikbo-07-22",
            exchange = "documentServiceExchange",
            routingKey = "documents.documentExistence"
        )

        producerChannelToCartService.exchangeDeclare(
            name = "documentServiceExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )
    }

    suspend fun declareBookingSetup() {
        producerChannelToBookingService.exchangeDeclare(
            name = "bookingServiceExchange",
            type = BuiltinExchangeType.FANOUT,
            durable = true
        )

        producerChannelToBookingService.queueDeclare(
            name = "PlaceAnOrderShivilovAY-ikbo-07-22",
            durable = false,
            exclusive = false,
            autoDelete = true
        )

        producerChannelToBookingService.queueBind(
            queue = "PlaceAnOrderShivilovAY-ikbo-07-22",
            exchange = "bookingServiceExchange",
            routingKey = ""
        )
    }

    suspend fun startConsumer() {
        val consumer = consumerChannel.basicConsume(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            noAck = true
        )

        for (delivery in consumer) {
            val message = delivery.message.body.decodeToString()
            val cartItemRequest = Json.decodeFromString<CartItem>(message)

            val isDocumentExisted = transaction {
                UserDocument.select {
                    (UserDocument.userId eq cartItemRequest.userId)
                }.singleOrNull() != null
            }

            val json = DocumentExistenceResponse(isDocumentExisted)
            val response = Json.encodeToString(json).toByteArray()

            producerChannelToCartService.basicPublish(
                body = response,
                exchange = "documentServiceExchange",
                routingKey = "documents.documentExistence"
            )

            if(isDocumentExisted) {
                val userId = cartItemRequest.userId
                val roomId = cartItemRequest.roomId
                val document : String = DocumentRepository.getDocumentByUserId(userId)
                val message = BookingRequestToRabbit(
                    userId = userId,
                    roomId = roomId,
                    document = document
                )

                val messageToBooking = Json.encodeToString(message).toByteArray()

                producerChannelToBookingService.basicPublish(
                    body = messageToBooking,
                    exchange = "bookingServiceExchange",
                    routingKey = ""
                )
            }
        }
    }

    suspend fun stopConnection() {
        connection.close()
        consumerChannel.close()
        producerChannelToBookingService.close()
        producerChannelToCartService.close()
    }
}