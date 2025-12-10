package com.example.rabbit

import com.example.database.insertToCart
import com.example.dto.request.CartItemRequestFromRabbit
import com.example.dto.response.DocumentExistenceResponse
import com.example.dto.response.OrderPlacingResponse
import dev.kourier.amqp.BuiltinExchangeType
import dev.kourier.amqp.channel.AMQPChannel
import dev.kourier.amqp.connection.AMQPConnection
import dev.kourier.amqp.connection.amqpConfig
import dev.kourier.amqp.connection.createAMQPConnection
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json

object RabbitSetup {
    lateinit var connection: AMQPConnection
    lateinit var consumerChannelFromUser: AMQPChannel
    lateinit var consumerChannelFromDocuments: AMQPChannel
    lateinit var producerChannel: AMQPChannel

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
        consumerChannelFromUser = connection.openChannel()
        consumerChannelFromDocuments = connection.openChannel()
        producerChannel = connection.openChannel()

        consumerChannelFromUser.exchangeDeclare(
            name = "RoomSelectedExchange",
            type = BuiltinExchangeType.DIRECT,
            durable = true
        )

        consumerChannelFromUser.queueDeclare(
            name = "RoomSelectedQueueOnishchukNI-ikbo-07-22",
            durable = true
        )

        consumerChannelFromUser.queueBind(
            queue = "RoomSelectedQueueOnishchukNI-ikbo-07-22",
            exchange = "RoomSelectedExchange",
            routingKey = "exampleKey"
        )

        consumerChannelFromDocuments.exchangeDeclare(
            name = "PlaceAnOrderExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )

        consumerChannelFromDocuments.queueDeclare(
            name = "PlaceAnOrderKomandinAY-ikbo-07-22",
            durable = true,
            exclusive = false,
            autoDelete = false
        )

        consumerChannelFromDocuments.queueBind(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            exchange = "PlaceAnOrderExchange",
            routingKey = "documents.documentExistence"
        )

        producerChannel.exchangeDeclare(
            name = "PlaceAnOrderExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )
    }

    suspend fun startConsumer() {
        val consumer = consumerChannelFromUser.basicConsume(
            queue = "RoomSelectedQueueOnishchukNI-ikbo-07-22",
            noAck = true,
        )

        for (delivery in consumer) {
            val message = delivery.message.body.decodeToString()
            val cartItemRequest = Json.decodeFromString<CartItemRequestFromRabbit>(message)

            insertToCart(cartItemRequest)
        }
    }

    suspend fun startConsumerFromDocuments(call: RoutingCall) {
        val consumer = consumerChannelFromDocuments.basicConsume(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            noAck = true,
        )

        for (delivery in consumer) {
            val message = delivery.message.body.decodeToString()
            val documentExistenceResponse = Json.decodeFromString<DocumentExistenceResponse>(message)

            call.respond(HttpStatusCode.OK, OrderPlacingResponse("${documentExistenceResponse.isDocumentExisted}"))
        }
    }

    suspend fun stopConnection() {
        connection.close()
        consumerChannelFromUser.close()
    }
}