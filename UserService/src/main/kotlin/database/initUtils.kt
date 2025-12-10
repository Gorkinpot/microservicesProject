package com.example.database

import com.example.database.Room.address
import com.example.database.Room.name
import com.example.database.Room.price
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun initUtils() {
    transaction {
        SchemaUtils.create(
            User,
            Room,
            CartItem,
            UserDocument
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

object User : Table("User") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val password = varchar("password", 50)

    override val primaryKey = PrimaryKey(id)
}

object Room : Table("Room") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val address = varchar("address", 100)
    val price = double("price")

    override val primaryKey = PrimaryKey(id)
}

object CartItem : Table("CartItem") {

    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val roomId = integer("room_id")

    init {
        foreignKey(userId to User.id, onDelete = ReferenceOption.CASCADE)
        foreignKey(roomId to Room.id, onDelete = ReferenceOption.CASCADE)
    }

    override val primaryKey = PrimaryKey(id)
}

object UserDocument : Table("UserDocument") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val document = varchar("document", 50)

    init {
        foreignKey(userId to User.id, onDelete = ReferenceOption.CASCADE)
    }

    override val primaryKey = PrimaryKey(id)
}