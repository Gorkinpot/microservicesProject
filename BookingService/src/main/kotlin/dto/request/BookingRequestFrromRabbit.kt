package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class BookingRequestFromRabbit(
    val userId: Int,
    val roomId: Int,
    val document : String
)