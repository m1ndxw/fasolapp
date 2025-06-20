package com.example.fasolapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedTaskDao {
    @Insert
    suspend fun insert(completedTask: CompletedTask)

    @Query("SELECT * FROM completed_tasks WHERE employeeId = :employeeId AND completionDate >= :startTime AND completionDate <= :endTime")
    fun getCompletedTasksByEmployee(employeeId: Int, startTime: Long, endTime: Long): Flow<List<CompletedTask>>

    @Query("SELECT * FROM completed_tasks WHERE completionDate >= :startTime AND completionDate <= :endTime")
    fun getAllCompletedTasks(startTime: Long, endTime: Long): Flow<List<CompletedTask>>
}