package com.example.database

import com.example.dto.request.CartItemRequestFromRabbit
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertToCart(cartItemRequest: CartItemRequestFromRabbit) {
    try {
        transaction {
            CartItem.insert {
                it[userId] = cartItemRequest.userId
                it[roomId] = cartItemRequest.roomId
            }
        }
    } catch (e: Exception) {
        println(e.message)
    }
}