package com.example.fasolapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val startTime: String?,
    val endTime: String?,
    val isWeekly: Boolean,
    val dayOfWeek: String?
)