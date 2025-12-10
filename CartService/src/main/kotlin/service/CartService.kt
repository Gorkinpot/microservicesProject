package com.example.service

import com.example.rabbit.RabbitSetup

object CartService {
    suspend fun publishToRabbit(message: ByteArray) {
        val channel = RabbitSetup.producerChannel
        channel.basicPublish(
            body = message,
            exchange = "PlaceAnOrderExchange",
            routingKey = ""
        )
    }
}