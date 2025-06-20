package com.example.fasolapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fasolapp.data.Employee

class DashboardViewModelFactory(private val application: Application, private val employee: Employee) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(application, employee) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TasksViewModelFactory(private val application: Application, private val employee: Employee) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(application, employee) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StatsViewModelFactory(private val application: Application, private val employee: Employee) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            return StatsViewModel(application, employee) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProfileViewModelFactory(private val application: Application, private val employee: Employee) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(application, employee) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AdminViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            return AdminViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}