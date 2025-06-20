package com.example.fasolapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "shifts",
    foreignKeys = [ForeignKey(
        entity = Employee::class,
        parentColumns = ["id"],
        childColumns = ["employeeId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Shift(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeId: Int,
    val startTime: Long,
    val endTime: Long?
)