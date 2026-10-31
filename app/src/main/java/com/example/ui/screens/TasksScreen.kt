package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.TaskCard
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.viewmodel.TaskFilter
import com.example.ui.viewmodel.UniFlowUiState
import com.example.ui.viewmodel.UniFlowViewModel

@Composable
fun TasksScreen(
    state: UniFlowUiState,
    viewModel: UniFlowViewModel,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        TaskFilter.ALL to "All (${state.tasks.size})",
        TaskFilter.PENDING to "Pending (${state.pendingTasksCount})",
        TaskFilter.HIGH_PRIORITY to "High Priority",
        TaskFilter.COMPLETED to "Completed (${state.tasks.size - state.pendingTasksCount})"
    )

    Scaffold(
        modifier = modifier.testTag("tasks_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.setShowAddTask(true) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                text = { Text("New Task") },
                modifier = Modifier
                    .testTag("add_task_fab")
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
            // Title Header
            item {
                Column {
                    Text(
                        text = "Assignments & Deadlines",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track course homework, semester projects & quiz prep",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_search_bar"),
                    placeholder = { Text("Search by task title or course...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { (filter, label) ->
                        FilterChip(
                            selected = state.taskFilter == filter,
                            onClick = { viewModel.setTaskFilter(filter) },
                            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Tasks List
            if (state.filteredTasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "No tasks",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Zero Tasks Found!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Everything in this category is complete or no matches found.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.filteredTasks) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { completed -> viewModel.toggleTask(task.id, completed) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }
}
