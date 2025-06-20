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
import com.example.fasolapp.viewmodel.AdminViewModel
import com.example.fasolapp.viewmodel.AdminViewModelFactory

@Composable
fun AdminScreen(navController: NavController, application: Application) {
    val viewModel: AdminViewModel = viewModel(factory = AdminViewModelFactory(application))
    val employees by viewModel.employees.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    var showEmployeeDialog by remember { mutableStateOf(false) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var employeeName by remember { mutableStateOf("") }
    var employeeLogin by remember { mutableStateOf("") }
    var employeePassword by remember { mutableStateOf("") }
    var employeeRole by remember { mutableStateOf("") }
    var taskName by remember { mutableStateOf("") }
    var taskStartTime by remember { mutableStateOf("") }
    var taskEndTime by remember { mutableStateOf("") }
    var isWeekly by remember { mutableStateOf(false) }
    var dayOfWeek by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Администрирование", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showEmployeeDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить сотрудника")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showTaskDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить задачу")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Сотрудники", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(employees) { employee ->
                EmployeeItem(employee, onEdit = { newEmployee ->
                    viewModel.updateEmployee(newEmployee)
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Задачи", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(tasks) { task ->
                AdminTaskItem(task, onEdit = { newTask ->
                    viewModel.updateTask(newTask)
                })
            }
        }
    }

    if (showEmployeeDialog) {
        AlertDialog(
            onDismissRequest = { showEmployeeDialog = false },
            title = { Text("Добавить сотрудника") },
            text = {
                Column {
                    OutlinedTextField(
                        value = employeeName,
                        onValueChange = { employeeName = it },
                        label = { Text("ФИО") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = employeeLogin,
                        onValueChange = { employeeLogin = it },
                        label = { Text("Логин") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = employeePassword,
                        onValueChange = { employeePassword = it },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = employeeRole,
                        onValueChange = { employeeRole = it },
                        label = { Text("Роль") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addEmployee(
                        Employee(
                            fullName = employeeName,
                            login = employeeLogin,
                            password = employeePassword,
                            role = employeeRole
                        )
                    )
                    showEmployeeDialog = false
                    employeeName = ""
                    employeeLogin = ""
                    employeePassword = ""
                    employeeRole = ""
                }) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                Button(onClick = { showEmployeeDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    if (showTaskDialog) {
        AlertDialog(
            onDismissRequest = { showTaskDialog = false },
            title = { Text("Добавить задачу") },
            text = {
                Column {
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = taskStartTime,
                        onValueChange = { taskStartTime = it },
                        label = { Text("Время начала (HH:mm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = taskEndTime,
                        onValueChange = { taskEndTime = it },
                        label = { Text("Время окончания (HH:mm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isWeekly,
                            onCheckedChange = { isWeekly = it }
                        )
                        Text("Еженедельная")
                    }
                    if (isWeekly) {
                        OutlinedTextField(
                            value = dayOfWeek,
                            onValueChange = { dayOfWeek = it },
                            label = { Text("День недели") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.addTask(
                        Task(
                            name = taskName,
                            startTime = taskStartTime.takeIf { it.isNotBlank() },
                            endTime = taskEndTime.takeIf { it.isNotBlank() },
                            isWeekly = isWeekly,
                            dayOfWeek = dayOfWeek.takeIf { isWeekly }
                        )
                    )
                    showTaskDialog = false
                    taskName = ""
                    taskStartTime = ""
                    taskEndTime = ""
                    isWeekly = false
                    dayOfWeek = ""
                }) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                Button(onClick = { showTaskDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun EmployeeItem(employee: Employee, onEdit: (Employee) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf(employee.fullName) }
    var login by remember { mutableStateOf(employee.login) }
    var password by remember { mutableStateOf(employee.password) }
    var role by remember { mutableStateOf(employee.role) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (isEditing) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("ФИО") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text("Логин") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Роль") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = {
                        onEdit(employee.copy(fullName = fullName, login = login, password = password, role = role))
                        isEditing = false
                    }) {
                        Text("Сохранить")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { isEditing = false }) {
                        Text("Отмена")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = employee.fullName, style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Логин: ${employee.login}")
                    Text(text = "Роль: ${employee.role}")
                }
                Button(onClick = { isEditing = true }) {
                    Text("Редактировать")
                }
            }
        }
    }
}

@Composable
fun AdminTaskItem(task: Task, onEdit: (Task) -> Unit) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(task.name) }
    var startTime by remember { mutableStateOf(task.startTime ?: "") }
    var endTime by remember { mutableStateOf(task.endTime ?: "") }
    var isWeekly by remember { mutableStateOf(task.isWeekly) }
    var dayOfWeek by remember { mutableStateOf(task.dayOfWeek ?: "") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (isEditing) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Время начала (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("Время окончания (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isWeekly,
                        onCheckedChange = { isWeekly = it }
                    )
                    Text("Еженедельная")
                }
                if (isWeekly) {
                    OutlinedTextField(
                        value = dayOfWeek,
                        onValueChange = { dayOfWeek = it },
                        label = { Text("День недели") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Button(onClick = {
                        onEdit(
                            task.copy(
                                name = name,
                                startTime = startTime.takeIf { it.isNotBlank() },
                                endTime = endTime.takeIf { it.isNotBlank() },
                                isWeekly = isWeekly,
                                dayOfWeek = dayOfWeek.takeIf { isWeekly }
                            )
                        )
                        isEditing = false
                    }) {
                        Text("Сохранить")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { isEditing = false }) {
                        Text("Отмена")
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = task.name, style = MaterialTheme.typography.bodyLarge)
                    if (task.startTime != null && task.endTime != null) {
                        Text(text = "${task.startTime}–${task.endTime}")
                    }
                    if (task.isWeekly) {
                        Text(text = "Еженедельная: ${task.dayOfWeek}")
                    }
                }
                Button(onClick = { isEditing = true }) {
                    Text("Редактировать")
                }
            }
        }
    }
}