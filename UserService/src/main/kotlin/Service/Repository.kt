package com.example.Service

import com.example.database.User
import com.example.dto.request.RegisterRequest
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object UserRepository {
    fun registerUser(req: RegisterRequest) {
        transaction {
            User.insert {
                it[User.username] = req.username
                it[User.password] = req.password
            }
        }
    }
}
