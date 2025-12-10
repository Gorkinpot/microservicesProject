package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val userId: Int,
    val roomId: Int
)