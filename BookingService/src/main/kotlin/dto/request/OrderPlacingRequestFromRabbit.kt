package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderPlacingRequestFromRabbit(
    val id: Int
)