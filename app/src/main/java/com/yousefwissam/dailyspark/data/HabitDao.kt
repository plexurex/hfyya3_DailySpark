package com.yousefwissam.dailyspark.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // Insert a habit and return the ID of the inserted row
    @Insert
    fun insert(habit: Habit): Long

    // Update a habit and return the number of rows affected
    @Update
    fun update(habit: Habit): Int

    // Delete a habit and return the number of rows affected
    @Delete
    fun delete(habit: Habit): Int

    // Get all habits as a Flow list
    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<Habit>>

    // Get a single habit by its ID as a Flow
    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Int): Flow<Habit>

    // Delete a habit by its ID and return the number of rows affected
    @Query("DELETE FROM habits WHERE id = :id")
    fun deleteHabitById(id: Int): Int
}
