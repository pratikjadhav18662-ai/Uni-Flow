package com.example.data.repository

import com.example.data.local.UniFlowDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class UniFlowRepository(private val db: UniFlowDatabase) {
    val allCourses: Flow<List<Course>> = db.courseDao().getAllCourses()
    val allSlots: Flow<List<ScheduleSlot>> = db.scheduleDao().getAllSlots()
    val allTasks: Flow<List<TaskItem>> = db.taskDao().getAllTasks()
    val pendingTasks: Flow<List<TaskItem>> = db.taskDao().getPendingTasks()
    val allExams: Flow<List<ExamItem>> = db.examDao().getAllExams()
    val attendanceLogs: Flow<List<AttendanceLog>> = db.attendanceDao().getAllLogs()

    fun getSlotsForDay(dayOfWeek: Int): Flow<List<ScheduleSlot>> =
        db.scheduleDao().getSlotsByDay(dayOfWeek)

    suspend fun insertCourse(course: Course): Long = db.courseDao().insertCourse(course)
    suspend fun updateCourse(course: Course) = db.courseDao().updateCourse(course)
    suspend fun deleteCourse(course: Course) {
        db.scheduleDao().deleteSlotsForCourse(course.id)
        db.courseDao().deleteCourse(course)
    }

    suspend fun markPresent(courseId: Long, courseName: String) {
        db.courseDao().markPresent(courseId)
        db.attendanceDao().insertLog(
            AttendanceLog(
                courseId = courseId,
                courseName = courseName,
                status = "PRESENT",
                note = "Attended scheduled lecture"
            )
        )
    }

    suspend fun markAbsent(courseId: Long, courseName: String) {
        db.courseDao().markAbsent(courseId)
        db.attendanceDao().insertLog(
            AttendanceLog(
                courseId = courseId,
                courseName = courseName,
                status = "ABSENT",
                note = "Marked absent / missed class"
            )
        )
    }

    suspend fun insertSlot(slot: ScheduleSlot): Long = db.scheduleDao().insertSlot(slot)
    suspend fun updateSlot(slot: ScheduleSlot) = db.scheduleDao().updateSlot(slot)
    suspend fun deleteSlot(slot: ScheduleSlot) = db.scheduleDao().deleteSlot(slot)

    suspend fun insertTask(task: TaskItem): Long = db.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskItem) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: TaskItem) = db.taskDao().deleteTask(task)
    suspend fun toggleTaskCompleted(taskId: Long, completed: Boolean) =
        db.taskDao().toggleTaskCompleted(taskId, completed)

    suspend fun insertExam(exam: ExamItem): Long = db.examDao().insertExam(exam)
    suspend fun deleteExam(exam: ExamItem) = db.examDao().deleteExam(exam)

    // Student Authentication
    suspend fun login(email: String, password: String): User? =
        db.userDao().login(email.trim(), password)

    suspend fun registerUser(user: User): Long =
        db.userDao().insertUser(user)

    suspend fun getUserByEmail(email: String): User? =
        db.userDao().getUserByEmail(email.trim())

    suspend fun updatePassword(email: String, newPassword: String): Boolean =
        db.userDao().updatePassword(email.trim(), newPassword) > 0

    suspend fun seedInitialData() {
        SampleDataSeeder.seedDatabaseIfEmpty(db)
    }
}
