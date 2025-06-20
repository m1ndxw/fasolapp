package com.example.fasolapp.repository

import android.content.Context
import androidx.room.Room
import com.example.fasolapp.data.AppDatabase
import com.example.fasolapp.data.CompletedTask
import com.example.fasolapp.data.Employee
import com.example.fasolapp.data.Shift
import com.example.fasolapp.data.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import java.util.Calendar

class AppRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "fasol-database"
    ).build()

    private val employeeDao = db.employeeDao()
    private val taskDao = db.taskDao()
    private val shiftDao = db.shiftDao()
    private val completedTaskDao = db.completedTaskDao()

    init {
        // Initialize test data
        runBlocking {
            withContext(Dispatchers.IO) {
                // Add test employees if none exist
                if (employeeDao.getAllEmployees().first().isEmpty()) {
                    employeeDao.insert(Employee(fullName = "Иван Иванов", login = "ivan", password = "123", role = "Cashier"))
                    employeeDao.insert(Employee(fullName = "Анна Петрова", login = "anna", password = "123", role = "Manager"))
                }
                // Initialize tasks
                resetDailyTasks()
            }
        }
    }

    suspend fun login(login: String, password: String): Employee? {
        return employeeDao.getEmployeeByLogin(login, password)
    }

    fun getAllEmployees(): Flow<List<Employee>> {
        return employeeDao.getAllEmployees()
    }

    suspend fun insertEmployee(employee: Employee) {
        employeeDao.insert(employee)
    }

    suspend fun updateEmployee(employee: Employee) {
        employeeDao.update(employee)
    }

    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    suspend fun insertTask(task: Task) {
        taskDao.insert(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun resetDailyTasks() {
        taskDao.deleteAll()
        val tasks = mutableListOf(
            Task(name = "Открытие кассы", startTime = null, endTime = null, isWeekly = false, dayOfWeek = null),
            Task(name = "Выкладка товара", startTime = "8:00", endTime = "16:00", isWeekly = false, dayOfWeek = null),
            Task(name = "Фасовка товара", startTime = "10:00", endTime = "18:00", isWeekly = false, dayOfWeek = null),
            Task(name = "Расстановка ценников", startTime = "8:00", endTime = "18:00", isWeekly = false, dayOfWeek = null),
            Task(name = "Уборка", startTime = "20:30", endTime = "22:00", isWeekly = false, dayOfWeek = null),
            Task(name = "Закрытие кассы", startTime = "21:30", endTime = "22:05", isWeekly = false, dayOfWeek = null),
            Task(name = "Выкладка сигарет", startTime = "11:00", endTime = "16:00", isWeekly = false, dayOfWeek = null)
        )
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            tasks.add(Task(name = "Очистка кофемашины", startTime = null, endTime = null, isWeekly = true, dayOfWeek = "Понедельник"))
        }
        tasks.forEach { taskDao.insert(it) }
    }

    suspend fun startShift(employeeId: Int): Shift? {
        val activeShift = shiftDao.getActiveShift(employeeId)
        if (activeShift == null) {
            val shift = Shift(employeeId = employeeId, startTime = System.currentTimeMillis(), endTime = null)
            shiftDao.insert(shift)
            return shift
        }
        return null
    }

    suspend fun endShift(employeeId: Int): Shift? {
        val activeShift = shiftDao.getActiveShift(employeeId)
        if (activeShift != null) {
            val updatedShift = activeShift.copy(endTime = System.currentTimeMillis())
            shiftDao.update(updatedShift)
            return updatedShift
        }
        return null
    }

    fun getShiftsByEmployee(employeeId: Int): Flow<List<Shift>> {
        return shiftDao.getShiftsByEmployee(employeeId)
    }

    suspend fun insertCompletedTask(completedTask: CompletedTask) {
        completedTaskDao.insert(completedTask)
    }

    fun getCompletedTasksByEmployee(employeeId: Int, startTime: Long, endTime: Long): Flow<List<CompletedTask>> {
        return completedTaskDao.getCompletedTasksByEmployee(employeeId, startTime, endTime)
    }

    fun getAllCompletedTasks(startTime: Long, endTime: Long): Flow<List<CompletedTask>> {
        return completedTaskDao.getAllCompletedTasks(startTime, endTime)
    }
}