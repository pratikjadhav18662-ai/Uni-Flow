package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Course
import com.example.data.model.ExamItem
import com.example.data.model.ScheduleSlot
import com.example.data.model.TaskItem
import com.example.ui.theme.CourseColors
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary

@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Course) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("3") }
    var targetAttendance by remember { mutableStateOf("75") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add University Course", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Course Name (e.g. Machine Learning)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_name_input"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code (e.g. CS401)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor / Professor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Classroom / Hall") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = credits,
                        onValueChange = { credits = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = targetAttendance,
                        onValueChange = { targetAttendance = it },
                        label = { Text("Target %") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text("Theme Color", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CourseColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColorIndex == index) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val hex = String.format("#%06X", (0xFFFFFF and CourseColors[selectedColorIndex].hashCode()))
                        onConfirm(
                            Course(
                                name = name.trim(),
                                code = code.trim().ifBlank { "GEN101" },
                                instructor = instructor.trim(),
                                location = location.trim().ifBlank { "Hall A" },
                                colorHex = hex,
                                credits = credits.toIntOrNull() ?: 3,
                                targetAttendancePercent = targetAttendance.toIntOrNull() ?: 75
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("submit_course_button")
            ) {
                Text("Save Course")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddSlotDialog(
    courses: List<Course>,
    selectedDayOfWeek: Int,
    onDismiss: () -> Unit,
    onConfirm: (ScheduleSlot) -> Unit
) {
    var selectedCourseIndex by remember { mutableIntStateOf(0) }
    var dayOfWeek by remember { mutableIntStateOf(selectedDayOfWeek) }
    var startTime by remember { mutableStateOf("09:00 AM") }
    var endTime by remember { mutableStateOf("10:30 AM") }
    var type by remember { mutableStateOf("Lecture") }
    var room by remember { mutableStateOf("Hall B-304") }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val types = listOf("Lecture", "Lab", "Tutorial", "Seminar")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Timetable Class Slot", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (courses.isNotEmpty()) {
                    Text("Select Course", style = MaterialTheme.typography.labelMedium)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        courses.forEachIndexed { index, course ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedCourseIndex = index }
                                    .background(
                                        if (selectedCourseIndex == index) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${course.code} - ${course.name}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                if (selectedCourseIndex == index) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Text("Day of Week", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    days.forEachIndexed { index, d ->
                        val dayNum = index + 1
                        val isSelected = dayOfWeek == dayNum
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { dayOfWeek = dayNum }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = d,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Text("Session Type", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    types.forEach { t ->
                        val isSelected = type == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { type = t }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Lab Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selCourse = courses.getOrNull(selectedCourseIndex)
                    if (selCourse != null) {
                        onConfirm(
                            ScheduleSlot(
                                courseId = selCourse.id,
                                courseName = selCourse.name,
                                courseCode = selCourse.code,
                                colorHex = selCourse.colorHex,
                                dayOfWeek = dayOfWeek,
                                startTime = startTime,
                                endTime = endTime,
                                type = type,
                                room = room,
                                instructor = selCourse.instructor
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("submit_slot_button")
            ) {
                Text("Add Class")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddTaskDialog(
    courses: List<Course>,
    onDismiss: () -> Unit,
    onConfirm: (TaskItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCourseIndex by remember { mutableIntStateOf(0) }
    var priority by remember { mutableStateOf("MEDIUM") }
    var type by remember { mutableStateOf("Assignment") }
    var daysDue by remember { mutableStateOf("3") }

    val priorities = listOf("HIGH", "MEDIUM", "LOW")
    val types = listOf("Assignment", "Project", "Lab Report", "Quiz", "Reading")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Assignment / Task", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (courses.isNotEmpty()) {
                    Text("Related Course", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        courses.take(3).forEachIndexed { index, course ->
                            val isSel = selectedCourseIndex == index
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedCourseIndex = index }
                            ) {
                                Text(
                                    text = course.code,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Text("Priority Level", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    priorities.forEach { p ->
                        val isSel = priority == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { priority = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = daysDue,
                    onValueChange = { daysDue = it },
                    label = { Text("Due in how many days?") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val selCourse = courses.getOrNull(selectedCourseIndex)
                        val days = daysDue.toLongOrNull() ?: 3L
                        val dueMillis = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
                        onConfirm(
                            TaskItem(
                                title = title.trim(),
                                description = description.trim(),
                                courseName = selCourse?.name ?: "General Academic",
                                courseId = selCourse?.id ?: 0L,
                                dueDateMillis = dueMillis,
                                priority = priority,
                                type = type
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("submit_task_button")
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddExamDialog(
    courses: List<Course>,
    onDismiss: () -> Unit,
    onConfirm: (ExamItem) -> Unit
) {
    var title by remember { mutableStateOf("Midterm Examination") }
    var venue by remember { mutableStateOf("Main Exam Hall") }
    var time by remember { mutableStateOf("09:30 AM") }
    var daysUntil by remember { mutableStateOf("10") }
    var weightage by remember { mutableStateOf("30") }
    var selectedCourseIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Schedule Exam", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Exam Venue / Hall") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Exam Time") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = daysUntil,
                    onValueChange = { daysUntil = it },
                    label = { Text("Days until Exam") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = weightage,
                    onValueChange = { weightage = it },
                    label = { Text("Grade Weightage % (e.g. 30)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selCourse = courses.getOrNull(selectedCourseIndex)
                    val days = daysUntil.toLongOrNull() ?: 7L
                    val dateMillis = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
                    onConfirm(
                        ExamItem(
                            title = title.trim(),
                            courseName = selCourse?.name ?: "Core Subject",
                            courseCode = selCourse?.code ?: "CS200",
                            examDateMillis = dateMillis,
                            time = time,
                            venue = venue,
                            weightagePercent = weightage.toIntOrNull() ?: 30
                        )
                    )
                }
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun InternshipGuideDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "UniFlow",
                                tint = Color.White
                            )
                        }
                        Column {
                            Text(
                                text = "UniFlow Project Dossier",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Internship, Viva & Play Store Guide",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section 1: Logo Concept
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🎓 1. Logo & Brand Concept",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Name: UniFlow (University + Workflow).\n" +
                                        "• Metaphor: Intertwines the traditional Academic Mortarboard with an energetic, glowing flow wave. It represents seamless mastery over university courses, timetable conflicts, and attendance targets without academic burnout.\n" +
                                        "• Color Palette: Midnight Indigo (#1E1B4B) signifying intellect and depth; Electric Violet (#6366F1) for focus; Cyan Accent (#0EA5E9) for fluid workflow; Emerald Success (#10B981) for attendance safety.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Section 2: Architecture & Database
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🏛️ 2. Architecture & Database (Room SQLite)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• MVVM Architecture: Clean separation of UI (Jetpack Compose), Domain Logic (UniFlowViewModel + Coroutines StateFlow), and Data Layer (UniFlowRepository).\n" +
                                        "• Room Database Tables:\n" +
                                        "  1. 'courses': id, name, code, instructor, credits, targetAttendancePercent, attendedClasses, totalClasses.\n" +
                                        "  2. 'schedule_slots': id, courseId, dayOfWeek (1..7), startTime, endTime, type, room.\n" +
                                        "  3. 'tasks': id, title, description, courseId, dueDateMillis, priority (HIGH/MED/LOW), isCompleted.\n" +
                                        "  4. 'exams': id, title, courseName, examDateMillis, venue, weightagePercent.\n" +
                                        "  5. 'attendance_logs': id, courseId, status (PRESENT/ABSENT), timestamp.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Section 3: Step-by-Step Android Studio Setup
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🚀 3. Beginner Android Studio Setup Instructions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Step 1: Download & Open Project\n" +
                                        "• Launch Android Studio (Ladybug or Hedgehog or newer).\n" +
                                        "• Click 'Open' -> navigate to and select this root folder.\n\n" +
                                        "Step 2: Gradle Sync & JDK Configuration\n" +
                                        "• Go to File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle.\n" +
                                        "• Set Gradle JDK to 'Embedded JDK 17' or 'JDK 21'.\n" +
                                        "• Click 'Sync Project with Gradle Files' (elephant icon).\n\n" +
                                        "Step 3: Run the App\n" +
                                        "• Select 'app' in the run configuration dropdown.\n" +
                                        "• Choose an Android Virtual Device (AVD with API 26+) or connect your physical Android phone with USB Debugging enabled.\n" +
                                        "• Click the green 'Run' (Play) button or press Shift+F10.\n\n" +
                                        "Step 4: Generate APK for Submission / Testing\n" +
                                        "• In top menu: Build -> Build Bundle(s) / APK(s) -> Build APK(s).\n" +
                                        "• Android Studio will output: app/build/outputs/apk/debug/app-debug.apk ready to install!\n\n" +
                                        "Step 5: Google Play Store Release (AAB)\n" +
                                        "• Build -> Generate Signed Bundle / APK -> Android App Bundle.\n" +
                                        "• Select or create your keystore -> upload to Google Play Console.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Section 4: Internship Viva / Interview Questions
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "💡 4. Top Internship / Viva Questions & Answers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Q: Why Jetpack Compose instead of XML Layouts?\n" +
                                        "A: Declarative UI eliminates findViewById and boilerplate adapters. UI state is driven reactively by StateFlow.\n\n" +
                                        "Q: How does the Bunk/Attendance Predictor calculate safe bunks?\n" +
                                        "A: We solve the inequality: (attendedClasses) / (totalClasses + x) >= targetPercent. Thus, x <= (attendedClasses / target) - totalClasses.\n\n" +
                                        "Q: How is local data kept persistent?\n" +
                                        "A: Room Database maps Kotlin entities to SQLite, with DAO queries returning asynchronous Kotlin Flows that automatically update UI when data changes.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
