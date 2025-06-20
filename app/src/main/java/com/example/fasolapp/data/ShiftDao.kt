package com.example.fasolapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Insert
    suspend fun insert(shift: Shift)

    @Update
    suspend fun update(shift: Shift)

    @Query("SELECT * FROM shifts WHERE employeeId = :employeeId AND endTime IS NULL")
    suspend fun getActiveShift(employeeId: Int): Shift?

    @Query("SELECT * FROM shifts WHERE employeeId = :employeeId AND endTime IS NOT NULL")
    fun getShiftsByEmployee(employeeId: Int): Flow<List<Shift>>
}