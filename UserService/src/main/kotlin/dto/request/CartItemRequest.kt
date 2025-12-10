package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItemRequest(
    val userId: String,
    val roomId: String
)