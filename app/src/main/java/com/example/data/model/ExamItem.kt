package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val courseName: String,
    val courseCode: String = "",
    val examDateMillis: Long,
    val time: String = "10:00 AM",
    val venue: String = "Main Exam Hall",
    val weightagePercent: Int = 30
) {
    fun daysRemaining(): Long {
        val now = System.currentTimeMillis()
        val diff = examDateMillis - now
        return (diff / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    }
}
