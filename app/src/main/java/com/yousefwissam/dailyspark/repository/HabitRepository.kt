package com.yousefwissam.dailyspark.repository

import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.HabitDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository(private val habitDao: HabitDao) {

    suspend fun insertHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.insertHabit(habit)
        }
    }

    suspend fun updateHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.updateHabit(habit)
        }
    }

    suspend fun deleteHabit(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.deleteHabit(habit)
        }
    }

    suspend fun getAllHabits(): List<Habit> {
        return withContext(Dispatchers.IO) {
            habitDao.getAllHabits()
        }
    }
}
