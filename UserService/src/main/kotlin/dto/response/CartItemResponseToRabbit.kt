package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CartItemResponseToRabbit(
    val userId: Int,
    val roomId: Int
)