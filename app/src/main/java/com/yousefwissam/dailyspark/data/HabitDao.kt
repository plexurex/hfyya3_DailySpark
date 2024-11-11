package com.yousefwissam.dailyspark.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: Habit)

    @Query("SELECT * FROM habit_table")
    fun getAllHabits(): Flow<List<Habit>>

    @Delete
    suspend fun delete(habit: Habit)
}
