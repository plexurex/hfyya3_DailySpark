package com.yousefwissam.dailyspark.repository

import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.data.HabitDao
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    fun getHabitById(id: Int): Flow<Habit> = habitDao.getHabitById(id)

    suspend fun insert(habit: Habit) {
        habitDao.insert(habit)
    }

    suspend fun deleteHabitById(id: Int) {
        habitDao.deleteHabitById(id)
    }

    suspend fun update(habit: Habit) {
        habitDao.update(habit)
    }
}
