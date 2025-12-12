package com.example.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class DocumentAddingRequest(
    val userId: Int,
    val document: String
)