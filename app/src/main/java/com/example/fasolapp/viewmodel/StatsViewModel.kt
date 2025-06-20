package com.example.fasolapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fasolapp.data.CompletedTask
import com.example.fasolapp.data.Employee
import com.example.fasolapp.data.Shift
import com.example.fasolapp.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class StatsViewModel(application: Application, private val employee: Employee) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _shifts = MutableStateFlow<List<Shift>>(emptyList())
    val shifts: StateFlow<List<Shift>> get() = _shifts
    private val _completedTasks = MutableStateFlow<List<CompletedTask>>(emptyList())
    val completedTasks: StateFlow<List<CompletedTask>> get() = _completedTasks
    private val _allEmployees = MutableStateFlow<List<Employee>>(emptyList())
    val allEmployees: StateFlow<List<Employee>> get() = _allEmployees
    private val _allCompletedTasks = MutableStateFlow<List<CompletedTask>>(emptyList())
    val allCompletedTasks: StateFlow<List<CompletedTask>> get() = _allCompletedTasks
    private val _period = MutableStateFlow(Period.WEEK)
    val period: StateFlow<Period> get() = _period

    enum class Period { WEEK, MONTH }

    init {
        viewModelScope.launch {
            repository.getShiftsByEmployee(employee.id).collectLatest { shifts ->
                _shifts.value = shifts
            }
            repository.getAllEmployees().collectLatest { employees ->
                _allEmployees.value = employees
            }
            updateData()
        }
    }

    fun setPeriod(period: Period) {
        _period.value = period
        viewModelScope.launch {
            updateData()
        }
    }

    private suspend fun updateData() {
        val (startTime, endTime) = when (_period.value) {
            Period.WEEK -> getWeekRange()
            Period.MONTH -> getMonthRange()
        }
        repository.getCompletedTasksByEmployee(employee.id, startTime, endTime).collectLatest { tasks ->
            _completedTasks.value = tasks
        }
        repository.getAllCompletedTasks(startTime, endTime).collectLatest { tasks ->
            _allCompletedTasks.value = tasks
        }
    }

    private fun getWeekRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return startTime to calendar.timeInMillis
    }

    private fun getMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return startTime to calendar.timeInMillis
    }
}