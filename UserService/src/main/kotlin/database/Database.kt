package com.example.database

import org.jetbrains.exposed.sql.Database

fun connectDatabase() {
    Database.connect(
        url = "jdbc:postgresql://db:5432/db",
        driver = "org.postgresql.Driver",
        user = "user",
        password = "pass"
    )

    initUtils()
}




