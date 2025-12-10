package com.example.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

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