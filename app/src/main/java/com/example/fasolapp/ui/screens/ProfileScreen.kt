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
import com.example.fasolapp.data.Shift
import com.example.fasolapp.viewmodel.ProfileViewModel
import com.example.fasolapp.viewmodel.ProfileViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(navController: NavController, employee: Employee, application: Application) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(application, employee))
    val shifts by viewModel.shifts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Профиль", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "ФИО: ${viewModel.employeeName}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Логин: ${viewModel.employeeLogin}")
        Text(text = "Роль: ${viewModel.employeeRole}")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "История смен", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(shifts) { shift ->
                ShiftItem(shift)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти")
        }
    }
}

@Composable
fun ShiftItem(shift: Shift) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Начало: ${dateFormat.format(Date(shift.startTime))}")
            Text(text = "Конец: ${shift.endTime?.let { dateFormat.format(Date(it)) } ?: "Активна"}")
        }
    }
}