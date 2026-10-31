package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ScheduleSlot
import com.example.ui.components.ClassCard
import com.example.ui.components.StatCard
import com.example.ui.components.TaskCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.UniFlowUiState
import com.example.ui.viewmodel.UniFlowViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    state: UniFlowUiState,
    viewModel: UniFlowViewModel,
    modifier: Modifier = Modifier
) {
    val todayFormatted = rememberFormattedDate()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Greeting & Next Class Card
        item {
            HeroNextClassCard(
                state = state,
                todayFormatted = todayFormatted,
                onMarkPresent = { slot ->
                    viewModel.markPresent(slot.courseId, slot.courseName)
                },
                onViewTimetable = {
                    viewModel.selectTab(NavTab.TIMETABLE)
                }
            )
        }

        // 4 Key Academic KPI Stat Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Academic Metrics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Today's Classes",
                        value = "${state.todaySlots.size}",
                        subtitle = "Lectures & Labs",
                        icon = Icons.Default.Class,
                        accentColor = IndigoLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Pending Tasks",
                        value = "${state.pendingTasksCount}",
                        subtitle = "${state.tasks.count { it.priority == "HIGH" && !it.isCompleted }} urgent",
                        icon = Icons.Default.Assignment,
                        accentColor = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Attendance",
                        value = String.format("%.1f%%", state.overallAttendancePercent),
                        subtitle = if (state.overallAttendancePercent >= 75f) "Safe Status" else "Action Needed",
                        icon = Icons.Default.PieChart,
                        accentColor = if (state.overallAttendancePercent >= 75f) EmeraldSuccess else RoseDanger,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Projected CGPA",
                        value = String.format("%.2f", state.projectedCGPA),
                        subtitle = "Target: 4.00",
                        icon = Icons.Default.Star,
                        accentColor = VioletPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Exam Countdown Banner (if any exams scheduled)
        if (state.exams.isNotEmpty()) {
            item {
                val nextExam = state.exams.first()
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IndigoPrimary.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = "Exam",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = nextExam.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${nextExam.courseName} • ${nextExam.venue}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = IndigoPrimary
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${nextExam.daysRemaining()}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Days left",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Class Timeline
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { viewModel.selectTab(NavTab.TIMETABLE) }) {
                    Text("View Full Week")
                }
            }
        }

        if (state.todaySlots.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Weekend,
                            contentDescription = "No classes",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Classes Scheduled Today!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Take time to study, catch up on assignments, or relax.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(state.todaySlots) { slot ->
                ClassCard(
                    slot = slot,
                    onMarkPresent = { viewModel.markPresent(slot.courseId, slot.courseName) },
                    onMarkAbsent = { viewModel.markAbsent(slot.courseId, slot.courseName) },
                    onDelete = { viewModel.deleteSlot(slot) }
                )
            }
        }

        // Urgent Assignments Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Priority Assignments",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { viewModel.selectTab(NavTab.TASKS) }) {
                    Text("All Tasks (${state.pendingTasksCount})")
                }
            }
        }

        val priorityTasks = state.tasks.filter { !it.isCompleted }.take(3)
        if (priorityTasks.isEmpty()) {
            item {
                Text(
                    text = "🎉 All caught up! No pending assignments.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldSuccess
                )
            }
        } else {
            items(priorityTasks) { task ->
                TaskCard(
                    task = task,
                    onToggle = { completed -> viewModel.toggleTask(task.id, completed) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }
    }
}

@Composable
private fun HeroNextClassCard(
    state: UniFlowUiState,
    todayFormatted: String,
    onMarkPresent: (ScheduleSlot) -> Unit,
    onViewTimetable: () -> Unit
) {
    val nextSlot = state.nextUpcomingSlot

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_next_class_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(MidnightIndigo, IndigoPrimary)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = todayFormatted.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            letterSpacing = 1.sp
                        )
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "NEXT CLASS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (nextSlot != null) {
                    Text(
                        text = nextSlot.courseName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${nextSlot.courseCode} • ${nextSlot.type} • ${nextSlot.room}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Time",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${nextSlot.startTime} - ${nextSlot.endTime}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Button(
                            onClick = { onMarkPresent(nextSlot) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanAccent,
                                contentColor = MidnightIndigo
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Mark Present",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Attended", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text(
                        text = "No Classes Today!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Enjoy your break or organize your schedule for tomorrow.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onViewTimetable,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Text("Add Class to Timetable")
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberFormattedDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    return sdf.format(Date())
}
