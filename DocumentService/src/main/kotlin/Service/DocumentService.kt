package com.example.Service

import com.example.dto.request.DocumentAddingRequest

object DocumentService {
    fun addDocument(request: DocumentAddingRequest) {
        DocumentRepository.addDocument(request)
    }
}