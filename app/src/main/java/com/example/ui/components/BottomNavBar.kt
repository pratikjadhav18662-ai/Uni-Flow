package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.NavTab

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .testTag("bottom_nav_bar")
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(NavTab.DASHBOARD, Icons.Default.Dashboard, "dashboard_tab"),
            Triple(NavTab.TIMETABLE, Icons.Default.CalendarToday, "timetable_tab"),
            Triple(NavTab.TASKS, Icons.Default.Assignment, "tasks_tab"),
            Triple(NavTab.ATTENDANCE, Icons.Default.PieChart, "attendance_tab"),
            Triple(NavTab.TOOLS, Icons.Default.AutoAwesome, "tools_tab")
        )

        items.forEach { (tab, icon, testTag) ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = icon, contentDescription = tab.title) },
                label = { Text(text = tab.title, style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.testTag(testTag),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
