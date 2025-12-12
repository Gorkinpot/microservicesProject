package com.example.Service

import com.example.database.UserDocument
import com.example.dto.request.DocumentAddingRequest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object DocumentRepository {
    fun addDocument(request: DocumentAddingRequest) {
        try {
            transaction {
                UserDocument.insert {
                    it[UserDocument.userId] = request.userId
                    it[UserDocument.document] = request.document
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun getDocumentByUserId(userId: Int): String {
        return transaction {
            UserDocument
                .select { UserDocument.userId eq userId }
                .map { it[UserDocument.document] }
                .first()
        }
    }
}
