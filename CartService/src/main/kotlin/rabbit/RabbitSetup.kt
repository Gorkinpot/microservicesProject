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
    lateinit var userServiceChannel: AMQPChannel
    lateinit var documentServiceConsumerChannel: AMQPChannel
    lateinit var documentServiceProducerChannel: AMQPChannel

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
        userServiceChannel = connection.openChannel()
        documentServiceConsumerChannel = connection.openChannel()
        documentServiceProducerChannel = connection.openChannel()

        declareUserSetup()
        declareDocumentSetup()
    }

    suspend fun startConsumer() {
        val consumer = userServiceChannel.basicConsume(
            queue = "RoomSelectedQueueOnishchukNI-ikbo-07-22",
            noAck = true,
        )

        try {
            for (delivery in consumer) {
                val message = delivery.message.body.decodeToString()
                val cartItemRequest = Json.decodeFromString<CartItemRequestFromRabbit>(message)

                println(cartItemRequest)

                insertToCart(cartItemRequest)
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    suspend fun startConsumerFromDocuments(call: RoutingCall) {
        val consumer = documentServiceConsumerChannel.basicConsume(
            queue = "PlaceAnOrderKuklinMA-ikbo-07-22",
            noAck = true,
        )

        val delivery = consumer.receive()
        val message = delivery.message.body.decodeToString()
        val documentExistenceResponse = Json.decodeFromString<DocumentExistenceResponse>(message)

        if(documentExistenceResponse.isDocumentExisted) {
            call.respond(HttpStatusCode.OK, OrderPlacingResponse("Document exists"))
        } else {
            call.respond(HttpStatusCode.NotFound, OrderPlacingResponse("Document does not exist"))
        }

        consumer.cancel()
    }

    suspend fun stopConnection() {
        connection.close()
        userServiceChannel.close()
        documentServiceProducerChannel.close()
        documentServiceConsumerChannel.close()
    }

    suspend fun declareUserSetup() {
        userServiceChannel.queueDeclare(
            name = "RoomSelectedQueueOnishchukNI-ikbo-07-22",
            durable = false,
            exclusive = true,
            autoDelete = true
        )

        userServiceChannel.queueBind(
            queue = "RoomSelectedQueueOnishchukNI-ikbo-07-22",
            exchange = "RoomSelectedExchange",
            routingKey = "exampleKey"
        )

        userServiceChannel.exchangeDeclare(
            name = "RoomSelectedExchange",
            type = BuiltinExchangeType.DIRECT,
            durable = true
        )
    }

    suspend fun declareDocumentSetup() {
        documentServiceProducerChannel.queueDeclare(
            name = "PlaceAnOrderKomandinAY-ikbo-07-22",
            durable = true,
            exclusive = false,
            autoDelete = false
        )

        documentServiceConsumerChannel.queueDeclare(
            name = "PlaceAnOrderKuklinMA-ikbo-07-22",
            durable = true,
            exclusive = false,
            autoDelete = false
        )

        documentServiceConsumerChannel.exchangeDeclare(
            name = "documentServiceExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )

        documentServiceConsumerChannel.queueBind(
            queue = "PlaceAnOrderKuklinMA-ikbo-07-22",
            exchange = "documentServiceExchange",
            routingKey = "documents.documentExistence"
        )


        documentServiceProducerChannel.exchangeDeclare(
            name = "documentServiceExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )

        documentServiceProducerChannel.queueBind(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            exchange = "documentServiceExchange",
            routingKey = "documents.documentCheck"
        )
    }
}