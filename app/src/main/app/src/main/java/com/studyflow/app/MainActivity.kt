package com.studyflow.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val PREFS = "studyflow_data"

data class ClassEntry(
    val subject: String,
    val date: String,
    val missed: Boolean
)

data class TimetableEntry(
    val day: String,
    val time: String,
    val subject: String,
    val room: String
)

data class StudyTask(
    val title: String,
    val deadline: String,
    val done: Boolean
)

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
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    var tab by remember { mutableStateOf("Home") }

    var subjects by remember {
        mutableStateOf(
            prefs.getString("subjects", "Anatomy,Physiology,Biochemistry,Histology")
                .orEmpty()
                .split(",")
                .filter { it.isNotBlank() }
        )
    }

    var attendance by remember {
        mutableStateOf(loadAttendance(prefs))
    }

    var timetable by remember {
        mutableStateOf(loadTimetable(prefs))
    }

    var tasks by remember {
        mutableStateOf(loadTasks(prefs))
    }

    var newSubject by remember { mutableStateOf("") }
    var newTask by remember { mutableStateOf("") }
    var taskDeadline by remember { mutableStateOf("") }

    var newDay by remember { mutableStateOf("Monday") }
    var newTime by remember { mutableStateOf("09:00") }
    var newClassSubject by remember { mutableStateOf("") }
    var newRoom by remember { mutableStateOf("") }

    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun saveSubjects() {
        prefs.edit().putString("subjects", subjects.joinToString(",")).apply()
    }

    fun saveAttendance() {
        val array = JSONArray()
        attendance.forEach {
            array.put(
                JSONObject().apply {
                    put("subject", it.subject)
                    put("date", it.date)
                    put("missed", it.missed)
                }
            )
        }
        prefs.edit().putString("attendance", array.toString()).apply()
    }

    fun saveTimetable() {
        val array = JSONArray()
        timetable.forEach {
            array.put(
                JSONObject().apply {
                    put("day", it.day)
                    put("time", it.time)
                    put("subject", it.subject)
                    put("room", it.room)
                }
            )
        }
        prefs.edit().putString("timetable", array.toString()).apply()
    }

    fun saveTasks() {
        val array = JSONArray()
        tasks.forEach {
            array.put(
                JSONObject().apply {
                    put("title", it.title)
                    put("deadline", it.deadline)
                    put("done", it.done)
                }
            )
        }
        prefs.edit().putString("tasks", array.toString()).apply()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf("Home", "Attendance", "Timetable", "Tasks").forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = {},
                        label = { Text(item) }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "STUDYFLOW",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (tab) {
                "Home" -> {
                    item {
                        Text(
                            "Your study day, organised.",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        InfoCard(
                            "Attendance",
                            "Subjects: ${subjects.size}\nMissed classes: ${attendance.count { it.missed }}"
                        )
                    }

                    item {
                        InfoCard(
                            "Timetable",
                            "${timetable.size} scheduled classes"
                        )
                    }

                    item {
                        InfoCard(
                            "Tasks",
                            "${tasks.count { !it.done }} tasks remaining\n${tasks.count { it.done }} completed"
                        )
                    }

                    item {
                        Button(
                            onClick = { tab = "Attendance" },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage Attendance")
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = { tab = "Timetable" },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Timetable")
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = { tab = "Tasks" },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Open Tasks & Assignments")
                        }
                    }
                }

                "Attendance" -> {
                    item {
                        Text(
                            "Attendance Management",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Date: $today")
                        Text(
                            "Mark only the classes you MISSED. " +
                                "Unmarked classes are not counted as attended."
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = newSubject,
                            onValueChange = { newSubject = it },
                            label = { Text("Add a subject") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val name = newSubject.trim()
                                if (name.isNotEmpty() &&
                                    !subjects.any { it.equals(name, true) }
                                ) {
                                    subjects = subjects + name
                                    saveSubjects()
                                    newSubject = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Subject")
                        }
                    }

                    items(subjects, key = { it }) { subject ->
                        val missedToday = attendance.any {
                            it.subject == subject && it.date == today && it.missed
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    subject,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                val total = attendance.count {
                                    it.subject == subject
                                }
                                val missed = attendance.count {
                                    it.subject == subject && it.missed
                                }
                                val attended = total - missed
                                val percent = if (total > 0) {
                                    attended * 100 / total
                                } else {
                                    0
                                }

                                Text("Recorded classes: $total")
                                Text("Missed: $missed")
                                Text("Attendance: $percent%")

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Missed today")
                                    Checkbox(
                                        checked = missedToday,
                                        onCheckedChange = { checked ->
                                            attendance = attendance.filterNot {
                                                it.subject == subject &&
                                                    it.date == today
                                            }

                                            if (checked) {
                                                attendance = attendance +
                                                    ClassEntry(subject, today, true)
                                            }

                                            saveAttendance()
                                        }
                                    )
                                }

                                TextButton(
                                    onClick = {
                                        subjects = subjects.filterNot { it == subject }
                                        saveSubjects()
                                    }
                                ) {
                                    Text("Remove subject")
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Attendance history",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    if (attendance.isEmpty()) {
                        item { Text("No attendance records yet.") }
                    } else {
                        items(
                            attendance.sortedByDescending { it.date },
                            key = { "${it.subject}_${it.date}" }
                        ) { record ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(record.subject)
                                        Text(record.date)
                                    }
                                    Text(if (record.missed) "Missed" else "Attended")
                                }
                            }
                        }
                    }
                }

                "Timetable" -> {
                    item {
                        Text(
                            "Weekly Timetable",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    item {
                        var dayExpanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = dayExpanded,
                            onExpandedChange = { dayExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = newDay,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Day") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = dayExpanded
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = dayExpanded,
                                onDismissRequest = { dayExpanded = false }
                            ) {
                                listOf(
                                    "Monday", "Tuesday", "Wednesday",
                                    "Thursday", "Friday", "Saturday", "Sunday"
                                ).forEach { day ->
                                    DropdownMenuItem(
                                        text = { Text(day) },
                                        onClick = {
                                            newDay = day
                                            dayExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = newTime,
                            onValueChange = { newTime = it },
                            label = { Text("Time (e.g. 09:00)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newClassSubject,
                            onValueChange = { newClassSubject = it },
                            label = { Text("Subject / Class") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = newRoom,
                            onValueChange = { newRoom = it },
                            label = { Text("Room (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (newClassSubject.isNotBlank() &&
                                    newTime.isNotBlank()
                                ) {
                                    timetable = timetable + TimetableEntry(
                                        newDay,
                                        newTime.trim(),
                                        newClassSubject.trim(),
                                        newRoom.trim()
                                    )
                                    saveTimetable()
                                    newClassSubject = ""
                                    newRoom = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Class")
                        }
                    }

                    if (timetable.isEmpty()) {
                        item { Text("Your timetable is empty. Add your classes above.") }
                    } else {
                        val days = listOf(
                            "Monday", "Tuesday", "Wednesday",
                            "Thursday", "Friday", "Saturday", "Sunday"
                        )

                        days.forEach { day ->
                            val entries = timetable
                                .filter { it.day == day }
                                .sortedBy { it.time }

                            if (entries.isNotEmpty()) {
                                item {
                                    Text(
                                        day,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                items(
                                    entries,
                                    key = {
                                        "${it.day}_${it.time}_${it.subject}_${it.room}"
                                    }
                                ) { entry ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    entry.subject,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text("${entry.time} • ${entry.room}")
                                            }

                                            TextButton(
                                                onClick = {
                                                    timetable = timetable.filterNot {
                                                        it == entry
                                            
