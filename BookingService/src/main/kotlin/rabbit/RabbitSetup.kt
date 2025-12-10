package com.example.rabbit

import com.example.database.UserDocument
import com.example.dto.request.OrderPlacingRequestFromRabbit
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

        consumerChannel.exchangeDeclare(
            name = "PlaceAnOrderExchange",
            type = BuiltinExchangeType.FANOUT,
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
            exchange = "PlaceAnOrderExchange",
            routingKey = ""
        )
    }

    suspend fun startConsumer() {
        val consumer = consumerChannel.basicConsume(
            queue = "PlaceAnOrderKomandinAY-ikbo-07-22",
            noAck = true,
        )

        for (delivery in consumer) {
            val message = delivery.message.body.decodeToString()
            val cartItemRequest = Json.decodeFromString<OrderPlacingRequestFromRabbit>(message)

            try {
                val isDocumentExisted = transaction {
                    UserDocument.select {
                        (UserDocument.id eq cartItemRequest.id)
                    }.singleOrNull() != null
                }

                if(isDocumentExisted) {

                } else {

                }
            } catch (e: Exception) {

            }
        }
    }

    suspend fun stopConnection() {
        connection.close()
        consumerChannel.close()
    }
}