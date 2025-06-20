package com.example.fasolapp.ui.screens

import android.app.Application
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fasolapp.data.CompletedTask
import com.example.fasolapp.data.Employee
import com.example.fasolapp.data.Shift
import com.example.fasolapp.utils.PdfGenerator
import com.example.fasolapp.viewmodel.StatsViewModel
import com.example.fasolapp.viewmodel.StatsViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatsScreen(navController: NavController, employee: Employee, application: Application) {
    val viewModel: StatsViewModel = viewModel(factory = StatsViewModelFactory(application, employee))
    val shifts by viewModel.shifts.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val allEmployees by viewModel.allEmployees.collectAsState()
    val allCompletedTasks by viewModel.allCompletedTasks.collectAsState()
    val period by viewModel.period.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Статистика", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.setPeriod(StatsViewModel.Period.WEEK) },
                enabled = period != StatsViewModel.Period.WEEK
            ) {
                Text("Неделя")
            }
            Button(
                onClick = { viewModel.setPeriod(StatsViewModel.Period.MONTH) },
                enabled = period != StatsViewModel.Period.MONTH
            ) {
                Text("Месяц")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Время смен", style = MaterialTheme.typography.titleLarge)
        ShiftDurationChart(shifts = shifts)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Выполненные задачи", style = MaterialTheme.typography.titleLarge)
        TaskCompletionChart(completedTasks = completedTasks)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Сравнение сотрудников", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(allEmployees) { emp ->
                val tasksCount = allCompletedTasks.count { it.employeeId == emp.id }
                val totalHours = shifts.filter { it.employeeId == emp.id }
                    .sumOf { ((it.endTime ?: System.currentTimeMillis()) - it.startTime) / (1000 * 60 * 60) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = emp.fullName, style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Задачи: $tasksCount")
                        Text(text = "Часы: $totalHours")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                PdfGenerator.generateReport(application, allEmployees, shifts, allCompletedTasks, period)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Экспорт в PDF")
        }
    }
}

@Composable
fun ShiftDurationChart(shifts: List<Shift>) {
    val maxHeight = 300f
    val barWidth = 40f
    val maxDuration = shifts.maxOfOrNull {
        ((it.endTime ?: System.currentTimeMillis()) - it.startTime) / (1000 * 60 * 60).toFloat()
    }?.coerceAtLeast(1f) ?: 1f

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(16.dp)) {
        shifts.forEachIndexed { index, shift ->
            val duration = ((shift.endTime ?: System.currentTimeMillis()) - shift.startTime) / (1000 * 60 * 60).toFloat()
            val barHeight = (duration / maxDuration) * maxHeight
            drawRect(
                color = Color.Blue,
                topLeft = Offset(index * (barWidth + 10f), maxHeight - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun TaskCompletionChart(completedTasks: List<CompletedTask>) {
    val maxHeight = 300f
    val barWidth = 40f
    val tasksCount = completedTasks.size.toFloat()

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .padding(16.dp)) {
        drawRect(
            color = Color.Green,
            topLeft = Offset(0f, maxHeight - tasksCount * 10f),
            size = Size(barWidth, tasksCount * 10f)
        )
    }
}