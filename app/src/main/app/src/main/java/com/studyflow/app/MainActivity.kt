package com.studyflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                StudyFlowApp(applicationContext)
            }
        }
    }
}

@Composable
fun StudyFlowApp(context: Context) {
    val prefs = remember {
        context.getSharedPreferences("studyflow_data", Context.MODE_PRIVATE)
    }

    var tab by remember { mutableStateOf("Home") }

    var subjects by remember {
        mutableStateOf(
            prefs.getString("subjects", "Anatomy,Physiology,Biochemistry")
                ?.split(",") ?: listOf("Anatomy", "Physiology", "Biochemistry")
        )
    }

    var tasks by remember {
        mutableStateOf(
            prefs.getString("tasks", "")
                ?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    var timetable by remember {
        mutableStateOf(
            prefs.getString("timetable", "")
                ?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    var missed by remember {
        mutableStateOf(
            prefs.getString("missed", "")
                ?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
        )
    }

    var subjectInput by remember { mutableStateOf("") }
    var taskInput by remember { mutableStateOf("") }
    var classInput by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("Anatomy") }

    fun save(key: String, values: List<String>) {
        prefs.edit().putString(key, values.joinToString("\n")).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("STUDYFLOW") })
        },
        bottomBar = {
            NavigationBar {
                listOf("Home", "Attendance", "Timetable", "Tasks").forEach { name ->
                    NavigationBarItem(
                        selected = tab == name,
                        onClick = { tab = name },
                        icon = { Text(
                            when (name) {
                                "Home" -> "⌂"
                                "Attendance" -> "%"
                                "Timetable" -> "▦"
                                else -> "✓"
                            }
                        ) },
                        label = { Text(name) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (tab) {
                "Home" -> {
                    Text("Welcome to StudyFlow", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Your study day, organised.")
                    Spacer(Modifier.height(20.dp))
                    Text("Subjects: ${subjects.size}")
                    Text("Pending tasks: ${tasks.size}")
                    Text("Missed classes recorded: ${missed.size}")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { tab = "Attendance" }) {
                        Text("Open Attendance")
                    }
                    Button(onClick = { tab = "Timetable" }) {
                        Text("Open Timetable")
                    }
                    Button(onClick = { tab = "Tasks" }) {
                        Text("Open Tasks")
                    }
                }

                "Attendance" -> {
                    Text("Attendance Tracker", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("Add subjects, then record missed classes.")
                    Spacer(Modifier.height(12.dp))

                    Row {
                        OutlinedTextField(
                            value = subjectInput,
                            onValueChange = { subjectInput = it },
                            label = { Text("New subject") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val s = subjectInput.trim()
                            if (s.isNotEmpty() && s !in subjects) {
                                subjects = subjects + s
                                save("subjects", subjects)
                                subjectInput = ""
                            }
                        }) { Text("Add") }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Record a missed class:")
                    subjects.forEach { subject ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(subject, modifier = Modifier.weight(1f))
                            Button(onClick = {
                                val entry = "$subject|${System.currentTimeMillis()}"
                                missed = missed + entry
                                save("missed", missed)
                            }) { Text("Missed") }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Missed classes: ${missed.size}")
                    LazyColumn {
                        items(missed.reversed()) { entry ->
                            Text(
                                entry.substringBefore("|") +
                                    " — " + entry.substringAfter("|", ""),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                "Timetable" -> {
                    Text("Weekly Timetable", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = classInput,
                        onValueChange = { classInput = it },
                        label = { Text("Day | Time | Subject") },
                        placeholder = { Text("Monday | 09:00 | Anatomy") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = {
                        if (classInput.isNotBlank()) {
                            timetable = timetable + classInput.trim()
                            save("timetable", timetable)
                            classInput = ""
                        }
                    }) { Text("Add Class") }

                    LazyColumn {
                        items(timetable) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item, modifier = Modifier.weight(1f))
                                    TextButton(onClick = {
                                        timetable = timetable - item
                                        save("timetable", timetable)
                                    }) { Text("Delete") }
                                }
                            }
                        }
                    }
                }

                "Tasks" -> {
                    Text("Tasks & Assignments", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = taskInput,
                        onValueChange = { taskInput = it },
                        label = { Text("Assignment or task") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = {
                        if (taskInput.isNotBlank()) {
                            tasks = tasks + taskInput.trim()
                            save("tasks", tasks)
                            taskInput = ""
                        }
                    }) { Text("Add Task") }

                    LazyColumn {
                        items(tasks) { task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(task, modifier = Modifier.weight(1f))
                                    TextButton(onClick = {
                                        tasks = tasks - task
                                        save("tasks", tasks)
                                    }) { Text("Done") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
