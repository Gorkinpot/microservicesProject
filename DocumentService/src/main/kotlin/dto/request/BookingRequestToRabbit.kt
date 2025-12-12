package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestToRabbit(
    val userId: Int,
    val roomId: Int,
    val document : String
)