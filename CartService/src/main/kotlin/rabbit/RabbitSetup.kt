package com.example.rabbit

import com.example.database.CartItem
import com.example.dto.request.CartItemRequestFromRabbit
import dev.kourier.amqp.BuiltinExchangeType
import dev.kourier.amqp.channel.AMQPChannel
import dev.kourier.amqp.connection.AMQPConnection
import dev.kourier.amqp.connection.amqpConfig
import dev.kourier.amqp.connection.createAMQPConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object RabbitSetup {
    lateinit var connection: AMQPConnection
    lateinit var channel: AMQPChannel
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
        channel = connection.openChannel()

        channel.exchangeDeclare(
            name = "RoomSelectedExchange",
            type = BuiltinExchangeType.DIRECT,
            durable = true
        )

        channel.queueDeclare(
            name = "RoomSelectedQueue",
            durable = true
        )

        channel.queueBind(
            queue = "RoomSelectedQueue",
            exchange = "RoomSelectedExchange",
            routingKey = ""
        )
    }

    suspend fun startConsumer() {
        val consumer = channel.basicConsume(
            queue = "RoomSelectedQueue",
            noAck = true,
        )

        for (delivery in consumer) {
            val message = delivery.message.body.decodeToString()
            val cartItemRequest = Json.decodeFromString<CartItemRequestFromRabbit>(message)

            transaction {
                CartItem.insert {
                    it[userId] = cartItemRequest.userId
                    it[roomId] = cartItemRequest.roomId
                }
            }
        }
    }

    suspend fun stopConnection() {
        connection.close()
        channel.close()
    }
}