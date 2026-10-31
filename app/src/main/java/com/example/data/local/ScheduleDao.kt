package com.example.data.local

import androidx.room.*
import com.example.data.model.ScheduleSlot
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_slots ORDER BY startTime ASC")
    fun getAllSlots(): Flow<List<ScheduleSlot>>

    @Query("SELECT * FROM schedule_slots WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getSlotsByDay(dayOfWeek: Int): Flow<List<ScheduleSlot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: ScheduleSlot): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlots(slots: List<ScheduleSlot>)

    @Update
    suspend fun updateSlot(slot: ScheduleSlot)

    @Delete
    suspend fun deleteSlot(slot: ScheduleSlot)

    @Query("DELETE FROM schedule_slots WHERE courseId = :courseId")
    suspend fun deleteSlotsForCourse(courseId: Long)
}
