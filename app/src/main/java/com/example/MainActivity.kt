package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.UniFlowDatabase
import com.example.data.repository.UniFlowRepository
import com.example.ui.components.AppHeader
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.NavTab
import com.example.ui.viewmodel.UniFlowViewModel
import com.example.ui.viewmodel.UniFlowViewModelFactory

class MainActivity : ComponentActivity() {
    private val viewModel: UniFlowViewModel by viewModels {
        val database = UniFlowDatabase.getDatabase(applicationContext)
        val repository = UniFlowRepository(database)
        UniFlowViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UniFlowRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun UniFlowRoot(viewModel: UniFlowViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = state.isAuthenticated,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "auth_screen_transition"
    ) { authenticated ->
        if (!authenticated) {
            AuthScreen(
                state = state,
                viewModel = viewModel
            )
        } else {
            UniFlowApp(
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun UniFlowApp(viewModel: UniFlowViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle Android system back press: return to Dashboard if on a sub-screen
    BackHandler(enabled = state.currentTab != NavTab.DASHBOARD) {
        viewModel.selectTab(NavTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppHeader(
                studentName = state.studentName,
                semester = state.studentSemester,
                onGuideClick = { viewModel.setShowInternshipGuide(true) },
                onLogoutClick = { viewModel.logout() }
            )
        },
        bottomBar = {
            BottomNavBar(
                currentTab = state.currentTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = state.currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "screen_transition"
            ) { targetTab ->
                when (targetTab) {
                    NavTab.DASHBOARD -> DashboardScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    NavTab.TIMETABLE -> TimetableScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    NavTab.TASKS -> TasksScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    NavTab.ATTENDANCE -> AttendanceScreen(
                        state = state,
                        viewModel = viewModel
                    )
                    NavTab.TOOLS -> ToolsScreen(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // Interactive Dialogs
    if (state.showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { viewModel.setShowAddCourse(false) },
            onConfirm = { course -> viewModel.addNewCourse(course) }
        )
    }

    if (state.showAddSlotDialog) {
        AddSlotDialog(
            courses = state.courses,
            selectedDayOfWeek = state.selectedDayOfWeek,
            onDismiss = { viewModel.setShowAddSlot(false) },
            onConfirm = { slot -> viewModel.addNewSlot(slot) }
        )
    }

    if (state.showAddTaskDialog) {
        AddTaskDialog(
            courses = state.courses,
            onDismiss = { viewModel.setShowAddTask(false) },
            onConfirm = { task -> viewModel.addNewTask(task) }
        )
    }

    if (state.showAddExamDialog) {
        AddExamDialog(
            courses = state.courses,
            onDismiss = { viewModel.setShowAddExam(false) },
            onConfirm = { exam -> viewModel.addNewExam(exam) }
        )
    }

    if (state.showInternshipGuideDialog) {
        InternshipGuideDialog(
            onDismiss = { viewModel.setShowInternshipGuide(false) }
        )
    }
}
