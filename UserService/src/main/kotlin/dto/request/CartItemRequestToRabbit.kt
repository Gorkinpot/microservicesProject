package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequestToRabbit(
    val userId: Int,
    val roomId: Int
)