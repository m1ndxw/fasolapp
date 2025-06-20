package com.example.fasolapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fasolapp.data.Employee
import com.example.fasolapp.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application, private val employee: Employee) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _shifts = MutableStateFlow<List<com.example.fasolapp.data.Shift>>(emptyList())
    val shifts: StateFlow<List<com.example.fasolapp.data.Shift>> = _shifts

    val employeeName: String = employee.fullName
    val employeeLogin: String = employee.login
    val employeeRole: String = employee.role

    init {
        viewModelScope.launch {
            repository.getShiftsByEmployee(employee.id).collectLatest { shifts ->
                _shifts.value = shifts
            }
        }
    }
}