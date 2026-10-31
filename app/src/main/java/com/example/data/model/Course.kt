package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String,
    val instructor: String,
    val location: String,
    val colorHex: String,
    val targetAttendancePercent: Int = 75,
    val attendedClasses: Int = 0,
    val totalClasses: Int = 0,
    val credits: Int = 3,
    val targetGrade: String = "A",
    val currentScore: Double = 0.0
) {
    val attendancePercentage: Float
        get() = if (totalClasses == 0) 100f else (attendedClasses.toFloat() / totalClasses) * 100f

    val isAttendanceSafe: Boolean
        get() = attendancePercentage >= targetAttendancePercent

    // Calculate how many more classes can be missed before falling below target
    fun safeBunks(): Int {
        if (totalClasses == 0) return 0
        val target = targetAttendancePercent / 100f
        // attended / (total + x) >= target => attended / target >= total + x => x <= (attended / target) - total
        val maxTotal = (attendedClasses / target).toInt()
        val bunks = maxTotal - totalClasses
        return if (bunks > 0) bunks else 0
    }

    // Calculate how many consecutive classes must be attended to reach target
    fun classesToAttendToRecover(): Int {
        if (attendancePercentage >= targetAttendancePercent) return 0
        val target = targetAttendancePercent / 100f
        // (attended + y) / (total + y) >= target
        // attended + y >= target * total + target * y
        // y * (1 - target) >= target * total - attended
        // y >= (target * total - attended) / (1 - target)
        val numerator = (target * totalClasses) - attendedClasses
        val denominator = 1f - target
        if (denominator <= 0f) return 0
        return kotlin.math.ceil(numerator / denominator).toInt().coerceAtLeast(1)
    }
}
