package com.yousefwissam.dailyspark.data

import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insert(habit: Habit) {
        habitDao.insert(habit)
    }

    suspend fun delete(habit: Habit) {
        habitDao.delete(habit)
    }
}
