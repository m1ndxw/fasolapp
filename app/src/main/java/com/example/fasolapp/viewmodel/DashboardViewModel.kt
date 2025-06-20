package com.example.fasolapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fasolapp.data.CompletedTask
import com.example.fasolapp.data.Employee
import com.example.fasolapp.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(application: Application, private val employee: Employee) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _tasks = MutableStateFlow<List<com.example.fasolapp.data.Task>>(emptyList())
    val tasks: StateFlow<List<com.example.fasolapp.data.Task>> get() = _tasks
    private val _shiftActive = MutableStateFlow(false)
    val shiftActive: StateFlow<Boolean> get() = _shiftActive
    private val _completedTasks = MutableStateFlow(0)
    val completedTasks: StateFlow<Int> get() = _completedTasks

    val employeeName: String = employee.fullName
    val isAdmin: Boolean = employee.role == "Manager"

    init {
        viewModelScope.launch {
            repository.getAllTasks().collectLatest { tasks ->
                _tasks.value = tasks
            }
            repository.getCompletedTasksByEmployee(
                employee.id,
                getStartOfDay(),
                getEndOfDay()
            ).collectLatest { completed ->
                _completedTasks.value = completed.size
            }
            repository.getShiftsByEmployee(employee.id).collectLatest { shifts ->
                _shiftActive.value = shifts.any { it.endTime == null }
            }
            repository.resetDailyTasks()
        }
    }

    fun toggleShift() {
        viewModelScope.launch {
            if (_shiftActive.value) {
                repository.endShift(employee.id)
            } else {
                repository.startShift(employee.id)
            }
        }
    }

    fun completeTask(taskId: Int, comment: String?) {
        viewModelScope.launch {
            repository.insertCompletedTask(
                CompletedTask(
                    taskId = taskId,
                    employeeId = employee.id,
                    completionDate = System.currentTimeMillis(),
                    comment = comment
                )
            )
        }
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}