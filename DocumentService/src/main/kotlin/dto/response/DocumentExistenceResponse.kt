package com.example.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DocumentExistenceResponse(
    val isDocumentExisted: Boolean
)