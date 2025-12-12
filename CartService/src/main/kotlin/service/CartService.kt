package com.example.service

import com.example.rabbit.RabbitSetup

object CartService {
    suspend fun publishToRabbit(message: ByteArray) {
        val channel = RabbitSetup.documentServiceProducerChannel
        channel.basicPublish(
            body = message,
            exchange = "documentServiceExchange",
            routingKey = "documents.documentCheck"
        )
    }
}