package com.example.rabbit

import com.example.database.UserDocument
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
        consumerChannel = connection.openChannel()
        producerChannel = connection.openChannel()

        consumerChannel.exchangeDeclare(
            name = "documentServiceExchange",
            type = BuiltinExchangeType.TOPIC,
            durable = true
        )

        producerChannel.exchangeDeclare(
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

        producerChannel.queueDeclare(
            name = "PlaceAnOrderKuklinMA-ikbo-07-22",
            durable = true,
            exclusive = false,
            autoDelete = false
        )

        producerChannel.queueBind(
            queue = "PlaceAnOrderKuklinMA-ikbo-07-22",
            exchange = "documentServiceExchange",
            routingKey = "documents.documentExistence"
        )

        consumerChannel.queueBind(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            exchange = "documentServiceExchange",
            routingKey = "documents.documentCheck"
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

            println(isDocumentExisted)

            val json = DocumentExistenceResponse(isDocumentExisted)
            val response = Json.encodeToString(json).toByteArray()

            println(json)
            println(response)

            producerChannel.basicPublish(
                body = response,
                exchange = "documentServiceExchange",
                routingKey = "documents.documentExistence"
            )
        }
    }

    suspend fun stopConnection() {
        connection.close()
        consumerChannel.close()
    }
}