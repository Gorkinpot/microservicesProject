package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequestFromRabbit(
    val userId: Int,
    val roomId: Int
)