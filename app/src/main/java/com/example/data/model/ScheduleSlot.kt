package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_slots")
data class ScheduleSlot(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val courseName: String,
    val courseCode: String,
    val colorHex: String,
    val dayOfWeek: Int, // 1 = Monday, 2 = Tuesday, ... 7 = Sunday
    val startTime: String, // e.g. "09:00" or "09:00 AM"
    val endTime: String,   // e.g. "10:30" or "10:30 AM"
    val type: String = "Lecture", // Lecture, Lab, Tutorial, Seminar
    val room: String = "Hall A",
    val instructor: String = ""
)
