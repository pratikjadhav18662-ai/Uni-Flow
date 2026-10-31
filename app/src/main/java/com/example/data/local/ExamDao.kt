package com.example.data.local

import androidx.room.*
import com.example.data.model.ExamItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY examDateMillis ASC")
    fun getAllExams(): Flow<List<ExamItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<ExamItem>)

    @Update
    suspend fun updateExam(exam: ExamItem)

    @Delete
    suspend fun deleteExam(exam: ExamItem)
}
