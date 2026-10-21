package com.studyflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                StudyFlowApp()
            }
        }
    }
}

@Composable
fun StudyFlowApp() {
    var selectedTab by remember { mutableStateOf("Home") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("STUDYFLOW") }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf("Home", "Attendance", "Tasks").forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {},
                        label = { Text(tab) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome to StudyFlow",
                style = MaterialTheme.typography.headlineSmall
            )

            when (selectedTab) {
                "Home" -> {
                    Text("Your study day, organised.")
                    InfoCard("Attendance", "Track your classes")
                    InfoCard("Timetable", "Plan your weekly schedule")
                    InfoCard("Tasks", "Keep assignments organised")
                }

                "Attendance" -> {
                    Text("Attendance")
                    Text("Attendance tracking will be added next.")
                }

                "Tasks" -> {
                    Text("Tasks & Assignments")
                    Text("Your study tasks will appear here.")
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
