package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger

@Composable
fun AttendanceCard(
    course: Course,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pct = course.attendancePercentage
    val isSafe = course.isAttendanceSafe
    val statusColor = if (isSafe) EmeraldSuccess else RoseDanger

    val courseAccentColor = try {
        Color(android.graphics.Color.parseColor(course.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("attendance_card_${course.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            // Header Row: Course Code badge, name & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(courseAccentColor)
                    )

                    Column {
                        Text(
                            text = course.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${course.code} • ${course.instructor} • ${course.credits} Credits",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Course",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attendance Progress & Numbers Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f%%", pct),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = statusColor
                            )
                        )
                        Text(
                            text = " / req ${course.targetAttendancePercent}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }
                    Text(
                        text = "Attended ${course.attendedClasses} of ${course.totalClasses} classes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Mini circular badge / status indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isSafe) "SAFE" else "LOW",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            letterSpacing = 1.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linear Progress Bar
            LinearProgressIndicator(
                progress = { (pct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Smart Bunk Prediction Alert Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSafe) EmeraldSuccess.copy(alpha = 0.08f) else AmberWarning.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isSafe) Icons.Default.Check else Icons.Default.Warning,
                        contentDescription = "Bunk Calculator Info",
                        tint = if (isSafe) EmeraldSuccess else AmberWarning,
                        modifier = Modifier.size(18.dp)
                    )

                    val predictionText = if (isSafe) {
                        val bunks = course.safeBunks()
                        if (bunks > 0) {
                            "You can safely skip next $bunks class${if (bunks > 1) "es" else ""} without dropping below ${course.targetAttendancePercent}%."
                        } else {
                            "On the edge! Attend the next class to keep above ${course.targetAttendancePercent}%."
                        }
                    } else {
                        val needed = course.classesToAttendToRecover()
                        "Must attend next $needed consecutive class${if (needed > 1) "es" else ""} to reach ${course.targetAttendancePercent}% requirement."
                    }

                    Text(
                        text = predictionText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (isSafe) EmeraldSuccess else AmberWarning
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Attendance Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onMarkPresent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("present_btn_${course.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldSuccess
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Mark Present",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Present", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onMarkAbsent,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("absent_btn_${course.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RoseDanger
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(RoseDanger.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Mark Absent",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Absent", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
