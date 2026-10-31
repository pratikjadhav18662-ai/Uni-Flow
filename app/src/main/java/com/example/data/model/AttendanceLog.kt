package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_logs")
data class AttendanceLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val courseName: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val status: String, // "PRESENT", "ABSENT", "CANCELLED"
    val note: String = ""
)
