package com.example.data.local

import androidx.room.*
import com.example.data.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY code ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("UPDATE courses SET attendedClasses = attendedClasses + 1, totalClasses = totalClasses + 1 WHERE id = :courseId")
    suspend fun markPresent(courseId: Long)

    @Query("UPDATE courses SET totalClasses = totalClasses + 1 WHERE id = :courseId")
    suspend fun markAbsent(courseId: Long)
}
