package com.example.data.local

import androidx.room.*
import com.example.data.model.AttendanceLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_logs ORDER BY dateMillis DESC")
    fun getAllLogs(): Flow<List<AttendanceLog>>

    @Query("SELECT * FROM attendance_logs WHERE courseId = :courseId ORDER BY dateMillis DESC")
    fun getLogsForCourse(courseId: Long): Flow<List<AttendanceLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AttendanceLog): Long

    @Delete
    suspend fun deleteLog(log: AttendanceLog)
}
