package com.example.fasolapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Employee::class, Task::class, Shift::class, CompletedTask::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun taskDao(): TaskDao
    abstract fun shiftDao(): ShiftDao
    abstract fun completedTaskDao(): CompletedTaskDao
}