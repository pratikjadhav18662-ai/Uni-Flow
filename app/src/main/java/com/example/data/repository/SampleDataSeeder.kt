package com.example.data.repository

import com.example.data.local.UniFlowDatabase
import com.example.data.model.Course
import com.example.data.model.ExamItem
import com.example.data.model.ScheduleSlot
import com.example.data.model.TaskItem

import com.example.data.model.User

object SampleDataSeeder {
    suspend fun seedDatabaseIfEmpty(db: UniFlowDatabase) {
        // Seed default demo student if not present
        if (db.userDao().getUserByEmail("alex.morgan@campus.edu") == null) {
            db.userDao().insertUser(
                User(
                    id = 1,
                    fullName = "Alex Morgan",
                    email = "alex.morgan@campus.edu",
                    studentId = "UF-2024-8842",
                    passwordHash = "password123",
                    semester = "Year 2 • Semester IV",
                    major = "B.Tech Computer Science"
                )
            )
        }

        // Only seed courses if empty
        val existing = db.courseDao().getCourseById(1)
        if (existing != null) return

        val courses = listOf(
            Course(
                id = 1,
                name = "Data Structures & Algorithms",
                code = "CS201",
                instructor = "Dr. Aris Thorne",
                location = "Hall B-304",
                colorHex = "#4F46E5",
                targetAttendancePercent = 75,
                attendedClasses = 19,
                totalClasses = 21,
                credits = 4,
                targetGrade = "A",
                currentScore = 91.5
            ),
            Course(
                id = 2,
                name = "Operating Systems & Kernels",
                code = "CS204",
                instructor = "Dr. Evelyn Vance",
                location = "Lab 3",
                colorHex = "#0EA5E9",
                targetAttendancePercent = 75,
                attendedClasses = 14,
                totalClasses = 20,
                credits = 4,
                targetGrade = "A-",
                currentScore = 84.0
            ),
            Course(
                id = 3,
                name = "Database Management Systems",
                code = "CS208",
                instructor = "Prof. Liam Scott",
                location = "Tech Wing 101",
                colorHex = "#10B981",
                targetAttendancePercent = 80,
                attendedClasses = 16,
                totalClasses = 17,
                credits = 3,
                targetGrade = "A+",
                currentScore = 96.0
            ),
            Course(
                id = 4,
                name = "Computer Networks",
                code = "CS310",
                instructor = "Dr. Maya Lin",
                location = "Hall C-12",
                colorHex = "#8B5CF6",
                targetAttendancePercent = 75,
                attendedClasses = 13,
                totalClasses = 16,
                credits = 3,
                targetGrade = "B+",
                currentScore = 82.5
            ),
            Course(
                id = 5,
                name = "Discrete Mathematics",
                code = "MA202",
                instructor = "Prof. Alan Davies",
                location = "Math Hall 4",
                colorHex = "#F59E0B",
                targetAttendancePercent = 75,
                attendedClasses = 17,
                totalClasses = 20,
                credits = 4,
                targetGrade = "A",
                currentScore = 89.0
            )
        )
        db.courseDao().insertCourses(courses)

        // Seed Timetable Slots (1 = Mon ... 7 = Sun)
        val slots = listOf(
            // Monday
            ScheduleSlot(
                courseId = 1,
                courseName = "Data Structures & Algorithms",
                courseCode = "CS201",
                colorHex = "#4F46E5",
                dayOfWeek = 1,
                startTime = "09:00 AM",
                endTime = "10:30 AM",
                type = "Lecture",
                room = "Hall B-304",
                instructor = "Dr. Aris Thorne"
            ),
            ScheduleSlot(
                courseId = 2,
                courseName = "Operating Systems & Kernels",
                courseCode = "CS204",
                colorHex = "#0EA5E9",
                dayOfWeek = 1,
                startTime = "11:00 AM",
                endTime = "12:30 PM",
                type = "Lab",
                room = "Lab 3",
                instructor = "Dr. Evelyn Vance"
            ),
            ScheduleSlot(
                courseId = 5,
                courseName = "Discrete Mathematics",
                courseCode = "MA202",
                colorHex = "#F59E0B",
                dayOfWeek = 1,
                startTime = "02:00 PM",
                endTime = "03:30 PM",
                type = "Lecture",
                room = "Math Hall 4",
                instructor = "Prof. Alan Davies"
            ),
            // Tuesday
            ScheduleSlot(
                courseId = 3,
                courseName = "Database Management Systems",
                courseCode = "CS208",
                colorHex = "#10B981",
                dayOfWeek = 2,
                startTime = "10:00 AM",
                endTime = "11:30 AM",
                type = "Lecture",
                room = "Tech Wing 101",
                instructor = "Prof. Liam Scott"
            ),
            ScheduleSlot(
                courseId = 4,
                courseName = "Computer Networks",
                courseCode = "CS310",
                colorHex = "#8B5CF6",
                dayOfWeek = 2,
                startTime = "01:30 PM",
                endTime = "03:00 PM",
                type = "Lecture",
                room = "Hall C-12",
                instructor = "Dr. Maya Lin"
            ),
            // Wednesday
            ScheduleSlot(
                courseId = 1,
                courseName = "Data Structures & Algorithms",
                courseCode = "CS201",
                colorHex = "#4F46E5",
                dayOfWeek = 3,
                startTime = "09:00 AM",
                endTime = "10:30 AM",
                type = "Tutorial",
                room = "Hall B-304",
                instructor = "Dr. Aris Thorne"
            ),
            ScheduleSlot(
                courseId = 2,
                courseName = "Operating Systems & Kernels",
                courseCode = "CS204",
                colorHex = "#0EA5E9",
                dayOfWeek = 3,
                startTime = "11:00 AM",
                endTime = "12:30 PM",
                type = "Lecture",
                room = "Tech Wing 101",
                instructor = "Dr. Evelyn Vance"
            ),
            ScheduleSlot(
                courseId = 4,
                courseName = "Computer Networks",
                courseCode = "CS310",
                colorHex = "#8B5CF6",
                dayOfWeek = 3,
                startTime = "03:00 PM",
                endTime = "04:30 PM",
                type = "Lab",
                room = "NetLab 2",
                instructor = "Dr. Maya Lin"
            ),
            // Thursday
            ScheduleSlot(
                courseId = 3,
                courseName = "Database Management Systems",
                courseCode = "CS208",
                colorHex = "#10B981",
                dayOfWeek = 4,
                startTime = "10:00 AM",
                endTime = "11:30 AM",
                type = "Lab",
                room = "DB Lab 1",
                instructor = "Prof. Liam Scott"
            ),
            ScheduleSlot(
                courseId = 5,
                courseName = "Discrete Mathematics",
                courseCode = "MA202",
                colorHex = "#F59E0B",
                dayOfWeek = 4,
                startTime = "02:00 PM",
                endTime = "03:30 PM",
                type = "Tutorial",
                room = "Math Hall 4",
                instructor = "Prof. Alan Davies"
            ),
            // Friday
            ScheduleSlot(
                courseId = 1,
                courseName = "Data Structures & Algorithms",
                courseCode = "CS201",
                colorHex = "#4F46E5",
                dayOfWeek = 5,
                startTime = "09:30 AM",
                endTime = "11:00 AM",
                type = "Seminar",
                room = "Hall B-304",
                instructor = "Dr. Aris Thorne"
            ),
            ScheduleSlot(
                courseId = 2,
                courseName = "Operating Systems & Kernels",
                courseCode = "CS204",
                colorHex = "#0EA5E9",
                dayOfWeek = 5,
                startTime = "01:00 PM",
                endTime = "02:30 PM",
                type = "Lecture",
                room = "Hall C-12",
                instructor = "Dr. Evelyn Vance"
            )
        )
        db.scheduleDao().insertSlots(slots)

        // Seed Tasks
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        val tasks = listOf(
            TaskItem(
                title = "Red-Black Tree Balancing & Traversals",
                description = "Complete the insertion and deletion rotation algorithms with test cases in C++.",
                courseName = "Data Structures & Algorithms",
                courseId = 1,
                dueDateMillis = now + (2 * oneDay),
                priority = "HIGH",
                type = "Assignment",
                isCompleted = false,
                progress = 65
            ),
            TaskItem(
                title = "Round Robin & Priority Scheduling Simulator",
                description = "Simulate preemptive CPU burst scheduling and plot average waiting times.",
                courseName = "Operating Systems & Kernels",
                courseId = 2,
                dueDateMillis = now + (4 * oneDay),
                priority = "HIGH",
                type = "Project",
                isCompleted = false,
                progress = 25
            ),
            TaskItem(
                title = "B+ Tree Indexing & Query Optimization Report",
                description = "Analyze performance execution plans with and without composite indexes.",
                courseName = "Database Management Systems",
                courseId = 3,
                dueDateMillis = now + (6 * oneDay),
                priority = "MEDIUM",
                type = "Lab Report",
                isCompleted = false,
                progress = 0
            ),
            TaskItem(
                title = "Socket Programming: Multi-threaded Chat Server",
                description = "Implement non-blocking TCP socket communication with client broadcasting.",
                courseName = "Computer Networks",
                courseId = 4,
                dueDateMillis = now + (9 * oneDay),
                priority = "MEDIUM",
                type = "Assignment",
                isCompleted = false,
                progress = 10
            ),
            TaskItem(
                title = "Graph Theory & Euler Paths Problem Set",
                description = "Solve Chapter 8 exercises on Hamiltonian circuits and planar graph coloring.",
                courseName = "Discrete Mathematics",
                courseId = 5,
                dueDateMillis = now + (11 * oneDay),
                priority = "LOW",
                type = "Reading",
                isCompleted = true,
                progress = 100
            )
        )
        db.taskDao().insertTasks(tasks)

        // Seed Exams
        val exams = listOf(
            ExamItem(
                title = "Midterm Examination",
                courseName = "Data Structures & Algorithms",
                courseCode = "CS201",
                examDateMillis = now + (7 * oneDay),
                time = "09:30 AM",
                venue = "Auditorium Hall 1",
                weightagePercent = 30
            ),
            ExamItem(
                title = "Kernel Architecture & Virtual Memory Quiz",
                courseName = "Operating Systems & Kernels",
                courseCode = "CS204",
                examDateMillis = now + (14 * oneDay),
                time = "02:00 PM",
                venue = "Lab 3 Annex",
                weightagePercent = 20
            ),
            ExamItem(
                title = "Final Project Evaluation",
                courseName = "Database Management Systems",
                courseCode = "CS208",
                examDateMillis = now + (28 * oneDay),
                time = "10:00 AM",
                venue = "Tech Wing 101",
                weightagePercent = 40
            )
        )
        db.examDao().insertExams(exams)
    }
}
