package com.example.Service

import com.example.database.Booking
import com.example.dto.request.BookingCancelRequest
import com.example.dto.request.BookingRequestFromRabbit
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object BookingRepository {
    fun addBookingInfo(request: BookingRequestFromRabbit) {
        transaction {
            Booking.insert {
                it[userId] = request.userId
                it[roomId] = request.roomId
                it[document] = request.document
            }
        }
        println("BookingInfo added")
    }

    fun deleteBookingInfo(request: BookingCancelRequest) {
        transaction {
            Booking.deleteWhere {
                Booking.userId eq request.userId
            }
        }
        println("BookingInfo removed")
    }
}
