package com.example.database

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun initUtils() {
    transaction {
        exec("DROP TABLE IF EXISTS \"user\" CASCADE")

        SchemaUtils.drop(CartItem, UserDocument, Booking)

        SchemaUtils.create(
            User,
            Room,
            CartItem,
            UserDocument,
            Booking
        )

        val roomsToInsert = listOf(
            Triple("Conference Hall", "123 Main St", 100.0),
            Triple("Meeting Room", "456 Oak Ave", 50.0),
            Triple("VIP Suite", "789 Pine Rd", 200.0)
        )

        roomsToInsert.forEach { (nameValue, addressValue, priceValue) ->
            val exists = Room.select { Room.name eq nameValue }.count() > 0
            if (!exists) {
                Room.insert {
                    it[name] = nameValue
                    it[address] = addressValue
                    it[price] = priceValue
                }
            }
        }
    }

}