package com.example.fasolapp.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fasolapp.data.Employee
import com.example.fasolapp.data.Task
import com.example.fasolapp.viewmodel.DashboardViewModel
import com.example.fasolapp.viewmodel.DashboardViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun DashboardScreen(navController: NavController, employee: Employee, application: Application) {
    val viewModel: DashboardViewModel = viewModel(factory = DashboardViewModelFactory(application, employee))
    val tasks by viewModel.tasks.collectAsState()
    val shiftActive by viewModel.shiftActive.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf<Task?>(null) }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Привет, ${viewModel.employeeName}!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.toggleShift() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (shiftActive) "Завершить смену" else "Начать смену")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Задачи на сегодня ($completedTasks/${tasks.size})",
            style = MaterialTheme.typography.titleLarge
        )

        LazyColumn {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onComplete = { showDialog = task },
                    isCompleted = completedTasks > 0 && tasks.indexOf(task) < completedTasks
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("tasks/${Json.encodeToString(employee)}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Все задачи")
        }
        Button(
            onClick = { navController.navigate("stats/${Json.encodeToString(employee)}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Статистика")
        }
        if (viewModel.isAdmin) {
            Button(
                onClick = { navController.navigate("admin") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Администрирование")
            }
        }
        Button(
            onClick = { navController.navigate("profile/${Json.encodeToString(employee)}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Профиль")
        }
    }

    showDialog?.let { task ->
        AlertDialog(
            onDismissRequest = { showDialog = null },
            title = { Text("Комментарий к задаче") },
            text = {
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Комментарий") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.completeTask(task.id, comment)
                        showDialog = null
                        comment = ""
                    }
                }) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = null }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun TaskItem(task: Task, onComplete: () -> Unit, isCompleted: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.name, style = MaterialTheme.typography.bodyLarge)
                if (task.startTime != null && task.endTime != null) {
                    Text(
                        text = "${task.startTime}–${task.endTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (!isCompleted) {
                Button(onClick = onComplete) {
                    Text("Выполнить")
                }
            } else {
                Text(text = "Выполнено", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}