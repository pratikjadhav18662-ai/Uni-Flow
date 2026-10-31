package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.UniFlowRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class NavTab(val title: String) {
    DASHBOARD("Overview"),
    TIMETABLE("Timetable"),
    TASKS("Tasks"),
    ATTENDANCE("Attendance"),
    TOOLS("Tools & Guide")
}

enum class TaskFilter {
    ALL, PENDING, COMPLETED, HIGH_PRIORITY
}

enum class PomodoroMode(val minutes: Int, val label: String) {
    FOCUS(25, "Deep Focus"),
    SHORT_BREAK(5, "Short Break"),
    LONG_BREAK(15, "Long Break")
}

data class PomodoroState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedSessions: Int = 3
)

data class UniFlowUiState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val authErrorMessage: String? = null,
    val authSuccessMessage: String? = null,
    val showForgotPasswordDialog: Boolean = false,
    val currentTab: NavTab = NavTab.DASHBOARD,
    val selectedDayOfWeek: Int = currentDayOfWeekIndex(),
    val courses: List<Course> = emptyList(),
    val slots: List<ScheduleSlot> = emptyList(),
    val tasks: List<TaskItem> = emptyList(),
    val exams: List<ExamItem> = emptyList(),
    val taskFilter: TaskFilter = TaskFilter.ALL,
    val searchQuery: String = "",
    val pomodoro: PomodoroState = PomodoroState(),
    val studentName: String = "Alex Morgan",
    val studentMajor: String = "B.Tech Computer Science",
    val studentSemester: String = "Year 2 • Semester IV",
    val showAddCourseDialog: Boolean = false,
    val showAddSlotDialog: Boolean = false,
    val showAddTaskDialog: Boolean = false,
    val showAddExamDialog: Boolean = false,
    val showInternshipGuideDialog: Boolean = false
) {
    val overallAttendancePercent: Float
        get() {
            val total = courses.sumOf { it.totalClasses }
            val attended = courses.sumOf { it.attendedClasses }
            return if (total == 0) 100f else (attended.toFloat() / total) * 100f
        }

    val totalCredits: Int
        get() = courses.sumOf { it.credits }

    val projectedCGPA: Float
        get() {
            if (courses.isEmpty()) return 3.8f
            // Estimate grade point from score or target grade
            val totalPoints = courses.sumOf { course ->
                val point = when (course.targetGrade) {
                    "A+", "O" -> 4.0
                    "A" -> 3.9
                    "A-" -> 3.7
                    "B+" -> 3.3
                    "B" -> 3.0
                    else -> 3.5
                }
                point * course.credits
            }
            return if (totalCredits == 0) 3.8f else (totalPoints / totalCredits).toFloat()
        }

    val todaySlots: List<ScheduleSlot>
        get() = slots.filter { it.dayOfWeek == selectedDayOfWeek }

    val nextUpcomingSlot: ScheduleSlot?
        get() = todaySlots.firstOrNull() ?: slots.firstOrNull()

    val pendingTasksCount: Int
        get() = tasks.count { !it.isCompleted }

    val filteredTasks: List<TaskItem>
        get() {
            var list = when (taskFilter) {
                TaskFilter.ALL -> tasks
                TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
                TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
                TaskFilter.HIGH_PRIORITY -> tasks.filter { it.priority == "HIGH" }
            }
            if (searchQuery.isNotBlank()) {
                list = list.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                            it.courseName.contains(searchQuery, ignoreCase = true)
                }
            }
            return list
        }
}

private fun currentDayOfWeekIndex(): Int {
    val cal = Calendar.getInstance()
    // Calendar: SUNDAY = 1, MONDAY = 2 ... SATURDAY = 7
    return when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1
        Calendar.TUESDAY -> 2
        Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4
        Calendar.FRIDAY -> 5
        Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7
        else -> 1
    }
}

class UniFlowViewModel(private val repository: UniFlowRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UniFlowUiState())
    val uiState: StateFlow<UniFlowUiState> = _uiState.asStateFlow()

    private var pomodoroJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialData()
        }

        viewModelScope.launch {
            combine(
                repository.allCourses,
                repository.allSlots,
                repository.allTasks,
                repository.allExams
            ) { courses, slots, tasks, exams ->
                _uiState.update { current ->
                    current.copy(
                        courses = courses,
                        slots = slots,
                        tasks = tasks,
                        exams = exams
                    )
                }
            }.collect()
        }
    }

    fun selectTab(tab: NavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectDay(dayOfWeek: Int) {
        _uiState.update { it.copy(selectedDayOfWeek = dayOfWeek) }
    }

    fun setTaskFilter(filter: TaskFilter) {
        _uiState.update { it.copy(taskFilter = filter) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleTask(taskId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(taskId, completed)
        }
    }

    fun markPresent(courseId: Long, courseName: String) {
        viewModelScope.launch {
            repository.markPresent(courseId, courseName)
        }
    }

    fun markAbsent(courseId: Long, courseName: String) {
        viewModelScope.launch {
            repository.markAbsent(courseId, courseName)
        }
    }

    fun addNewCourse(course: Course) {
        viewModelScope.launch {
            repository.insertCourse(course)
            _uiState.update { it.copy(showAddCourseDialog = false) }
        }
    }

    fun addNewSlot(slot: ScheduleSlot) {
        viewModelScope.launch {
            repository.insertSlot(slot)
            _uiState.update { it.copy(showAddSlotDialog = false) }
        }
    }

    fun addNewTask(task: TaskItem) {
        viewModelScope.launch {
            repository.insertTask(task)
            _uiState.update { it.copy(showAddTaskDialog = false) }
        }
    }

    fun addNewExam(exam: ExamItem) {
        viewModelScope.launch {
            repository.insertExam(exam)
            _uiState.update { it.copy(showAddExamDialog = false) }
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun deleteSlot(slot: ScheduleSlot) {
        viewModelScope.launch {
            repository.deleteSlot(slot)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }

    // Pomodoro Timer Controls
    fun setPomodoroMode(mode: PomodoroMode) {
        pomodoroJob?.cancel()
        _uiState.update {
            it.copy(
                pomodoro = it.pomodoro.copy(
                    mode = mode,
                    secondsRemaining = mode.minutes * 60,
                    isRunning = false
                )
            )
        }
    }

    fun togglePomodoro() {
        val current = _uiState.value.pomodoro
        if (current.isRunning) {
            pomodoroJob?.cancel()
            _uiState.update { it.copy(pomodoro = current.copy(isRunning = false)) }
        } else {
            _uiState.update { it.copy(pomodoro = current.copy(isRunning = true)) }
            pomodoroJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    val s = _uiState.value.pomodoro.secondsRemaining
                    if (s > 1) {
                        _uiState.update {
                            it.copy(pomodoro = it.pomodoro.copy(secondsRemaining = s - 1))
                        }
                    } else {
                        // Timer completed
                        _uiState.update {
                            val newCompleted = if (it.pomodoro.mode == PomodoroMode.FOCUS) {
                                it.pomodoro.completedSessions + 1
                            } else it.pomodoro.completedSessions

                            val nextMode = if (it.pomodoro.mode == PomodoroMode.FOCUS) {
                                if (newCompleted % 4 == 0) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
                            } else {
                                PomodoroMode.FOCUS
                            }
                            it.copy(
                                pomodoro = PomodoroState(
                                    mode = nextMode,
                                    secondsRemaining = nextMode.minutes * 60,
                                    isRunning = false,
                                    completedSessions = newCompleted
                                )
                            )
                        }
                        break
                    }
                }
            }
        }
    }

    fun resetPomodoro() {
        pomodoroJob?.cancel()
        val mode = _uiState.value.pomodoro.mode
        _uiState.update {
            it.copy(
                pomodoro = it.pomodoro.copy(
                    secondsRemaining = mode.minutes * 60,
                    isRunning = false
                )
            )
        }
    }

    // Authentication Actions
    fun login(email: String, pass: String, onResult: (Boolean) -> Unit = {}) {
        _uiState.update { it.copy(authErrorMessage = null, authSuccessMessage = null) }
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter both email address and password.") }
            onResult(false)
            return
        }
        viewModelScope.launch {
            val user = repository.login(cleanEmail, pass)
            if (user != null) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentUser = user,
                        studentName = user.fullName,
                        studentMajor = user.major,
                        studentSemester = user.semester,
                        authErrorMessage = null,
                        authSuccessMessage = null
                    )
                }
                onResult(true)
            } else {
                _uiState.update { it.copy(authErrorMessage = "Invalid email or password. Please verify your credentials or register.") }
                onResult(false)
            }
        }
    }

    fun quickDemoLogin() {
        login("alex.morgan@campus.edu", "password123")
    }

    fun register(
        fullName: String,
        email: String,
        studentId: String,
        pass: String,
        confirmPass: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        _uiState.update { it.copy(authErrorMessage = null, authSuccessMessage = null) }
        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        val cleanId = studentId.trim()

        if (cleanName.isBlank()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter your full name.") }
            onResult(false)
            return
        }
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            _uiState.update { it.copy(authErrorMessage = "Please enter a valid university email address.") }
            onResult(false)
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(authErrorMessage = "Password must be at least 6 characters.") }
            onResult(false)
            return
        }
        if (pass != confirmPass) {
            _uiState.update { it.copy(authErrorMessage = "Passwords do not match. Please re-enter.") }
            onResult(false)
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByEmail(cleanEmail)
            if (existing != null) {
                _uiState.update { it.copy(authErrorMessage = "An account with this email already exists. Please log in.") }
                onResult(false)
                return@launch
            }

            val newUser = User(
                fullName = cleanName,
                email = cleanEmail,
                studentId = cleanId.ifBlank { "UF-" + (1000..9999).random() },
                passwordHash = pass,
                semester = "Year 1 • Semester I",
                major = "Computer Science"
            )
            val newId = repository.registerUser(newUser)
            val registeredUser = newUser.copy(id = newId)

            _uiState.update {
                it.copy(
                    isAuthenticated = true,
                    currentUser = registeredUser,
                    studentName = registeredUser.fullName,
                    studentMajor = registeredUser.major,
                    studentSemester = registeredUser.semester,
                    authErrorMessage = null,
                    authSuccessMessage = "Account registered successfully! Welcome to UniFlow."
                )
            }
            onResult(true)
        }
    }

    fun resetPassword(email: String, newPass: String, confirmPass: String, onResult: (Boolean) -> Unit = {}) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter your registered email address.") }
            onResult(false)
            return
        }
        if (newPass.length < 6) {
            _uiState.update { it.copy(authErrorMessage = "New password must be at least 6 characters.") }
            onResult(false)
            return
        }
        if (newPass != confirmPass) {
            _uiState.update { it.copy(authErrorMessage = "Passwords do not match.") }
            onResult(false)
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByEmail(cleanEmail)
            if (existing == null) {
                _uiState.update { it.copy(authErrorMessage = "No student account found with this email.") }
                onResult(false)
                return@launch
            }

            val success = repository.updatePassword(cleanEmail, newPass)
            if (success) {
                _uiState.update {
                    it.copy(
                        showForgotPasswordDialog = false,
                        authSuccessMessage = "Password reset successfully! Please log in with your new password.",
                        authErrorMessage = null
                    )
                }
                onResult(true)
            } else {
                _uiState.update { it.copy(authErrorMessage = "Failed to update password. Please try again.") }
                onResult(false)
            }
        }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                currentUser = null,
                authErrorMessage = null,
                authSuccessMessage = null
            )
        }
    }

    fun clearAuthMessages() {
        _uiState.update { it.copy(authErrorMessage = null, authSuccessMessage = null) }
    }

    fun setShowForgotPassword(show: Boolean) = _uiState.update {
        it.copy(showForgotPasswordDialog = show, authErrorMessage = null)
    }

    // Dialog toggles
    fun setShowAddCourse(show: Boolean) = _uiState.update { it.copy(showAddCourseDialog = show) }
    fun setShowAddSlot(show: Boolean) = _uiState.update { it.copy(showAddSlotDialog = show) }
    fun setShowAddTask(show: Boolean) = _uiState.update { it.copy(showAddTaskDialog = show) }
    fun setShowAddExam(show: Boolean) = _uiState.update { it.copy(showAddExamDialog = show) }
    fun setShowInternshipGuide(show: Boolean) = _uiState.update { it.copy(showInternshipGuideDialog = show) }
}

class UniFlowViewModelFactory(private val repository: UniFlowRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UniFlowViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UniFlowViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
