package com.example.fasolapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fasolapp.data.Employee
import com.example.fasolapp.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val employee: Employee) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val employee = repository.login(login, password)
            if (employee != null) {
                _loginState.value = LoginState.Success(employee)
            } else {
                _loginState.value = LoginState.Error("Неверный логин или пароль")
            }
        }
    }
}