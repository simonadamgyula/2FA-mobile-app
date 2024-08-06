package me.sim05.twofactorauth.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class Service (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val username: String,
    val token: String
)