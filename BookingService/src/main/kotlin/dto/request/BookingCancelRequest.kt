package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class BookingCancelRequest(
    val userId : Int
)