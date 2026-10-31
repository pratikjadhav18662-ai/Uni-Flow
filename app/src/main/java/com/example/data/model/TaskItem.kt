package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val courseName: String = "",
    val courseId: Long = 0,
    val dueDateMillis: Long,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, LOW
    val type: String = "Assignment", // Assignment, Quiz, Project, Lab Report, Reading
    val isCompleted: Boolean = false,
    val progress: Int = 0 // 0 to 100
) {
    val isOverdue: Boolean
        get() = !isCompleted && dueDateMillis < System.currentTimeMillis()

    fun dueStatusText(): String {
        val now = System.currentTimeMillis()
        val diff = dueDateMillis - now
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            isCompleted -> "Completed"
            diff < 0 -> "Overdue"
            hours < 24 -> "Due in $hours hrs"
            days == 1L -> "Due tomorrow"
            else -> "Due in $days days"
        }
    }
}
