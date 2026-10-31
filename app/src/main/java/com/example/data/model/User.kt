package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val studentId: String = "",
    val passwordHash: String,
    val semester: String = "Year 2 • Semester IV",
    val major: String = "B.Tech Computer Science"
)
