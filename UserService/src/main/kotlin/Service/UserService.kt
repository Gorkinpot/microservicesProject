package com.example.Service

import com.example.dto.request.AuthRequest
import com.example.dto.request.RegisterRequest
import com.example.rabbit.RabbitSetup

object UserService {
    fun register(req: RegisterRequest) {
        UserRepository.registerUser(req)
    }

    fun authorize(req: AuthRequest) : Boolean {
        return UserRepository.authorizeUser(req)
    }

    suspend fun publishToRabbit(message: ByteArray) {
        val channel = RabbitSetup.channel
        channel.basicPublish(
            body = message,
            exchange = "RoomSelectedExchange",
            routingKey = ""
        )
    }
}