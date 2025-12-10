package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderPlacingRequest(
    val id : Int
)