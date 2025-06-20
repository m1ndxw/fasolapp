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
import com.example.fasolapp.viewmodel.TasksViewModel
import com.example.fasolapp.viewmodel.TasksViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(navController: NavController, employee: Employee, application: Application) {
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModelFactory(application, employee))
    val tasks by viewModel.tasks.collectAsState()
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
        Text(text = "Задачи", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                val isCompleted = completedTasks.any { it.taskId == task.id }
                TaskItem(
                    task = task,
                    onComplete = { showDialog = task },
                    isCompleted = isCompleted
                )
            }
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