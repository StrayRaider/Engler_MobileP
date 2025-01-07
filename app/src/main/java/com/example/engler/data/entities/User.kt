package com.example.engler.data.entities
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    val username: String,

    val email: String,

    var totalScore: Int = 0,

    val createdAt: Long = System.currentTimeMillis(),

    var jwtToken: String? = null
)