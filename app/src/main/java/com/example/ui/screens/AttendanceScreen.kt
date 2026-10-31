package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.components.AttendanceCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.UniFlowUiState
import com.example.ui.viewmodel.UniFlowViewModel

@Composable
fun AttendanceScreen(
    state: UniFlowUiState,
    viewModel: UniFlowViewModel,
    modifier: Modifier = Modifier
) {
    val overallPct = state.overallAttendancePercent
    val isOverallSafe = overallPct >= 75f

    Scaffold(
        modifier = modifier.testTag("attendance_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowAddCourse(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Course") },
                text = { Text("Add Subject") },
                modifier = Modifier
                    .testTag("add_course_fab")
                    .padding(bottom = 80.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Attendance & Bunk Predictor",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time eligibility calculation for university exam thresholds",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Hero Overall Attendance Overview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = if (isOverallSafe) {
                                        listOf(MidnightIndigo, IndigoDark)
                                    } else {
                                        listOf(Color(0xFF7F1D1D), MidnightIndigo)
                                    }
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = "Eligibility",
                                        tint = if (isOverallSafe) EmeraldSuccess else RoseDanger,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = if (isOverallSafe) "EXAM ELIGIBLE (75%+)" else "CRITICAL ATTENDANCE ALERT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isOverallSafe) EmeraldSuccess else RoseDanger,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${state.courses.size} SUBJECTS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = String.format("%.1f%%", overallPct),
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            fontSize = 38.sp
                                        )
                                    )
                                    Text(
                                        text = "Aggregate University Attendance",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    )
                                }

                                val totalLectures = state.courses.sumOf { it.totalClasses }
                                val attendedLectures = state.courses.sumOf { it.attendedClasses }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$attendedLectures / $totalLectures",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent
                                        )
                                    )
                                    Text(
                                        text = "Classes Attended",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            LinearProgressIndicator(
                                progress = { (overallPct / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (isOverallSafe) EmeraldSuccess else RoseDanger,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }

            // Subject-by-Subject List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subject Breakdown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap +Present to log",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.courses.isEmpty()) {
                item {
                    Text(
                        text = "No subjects added yet. Tap '+ Add Subject' to start tracking attendance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(state.courses) { course ->
                    AttendanceCard(
                        course = course,
                        onMarkPresent = { viewModel.markPresent(course.id, course.name) },
                        onMarkAbsent = { viewModel.markAbsent(course.id, course.name) },
                        onDelete = { viewModel.deleteCourse(course) }
                    )
                }
            }
        }
    }
}
