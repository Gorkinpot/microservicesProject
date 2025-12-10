package com.example.Service

import com.example.database.User
import com.example.dto.request.AuthRequest
import com.example.dto.request.RegisterRequest
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object UserRepository {
    fun registerUser(req: RegisterRequest) {
        transaction {
            User.insert {
                it[User.email] = req.email
                it[User.username] = req.username
                it[User.password] = req.password
            }
        }
    }

    fun authorizeUser(req: AuthRequest): Boolean {
        return transaction {
            User.select {
                (User.username eq req.username) and (User.password eq req.password)
            }.singleOrNull() != null
        }
    }
}
