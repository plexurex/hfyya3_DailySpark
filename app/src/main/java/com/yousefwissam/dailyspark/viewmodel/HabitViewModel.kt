package com.yousefwissam.dailyspark.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.repository.HabitRepository
import com.yousefwissam.dailyspark.worker.ResetHabitWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HabitViewModel(
    private val repository: HabitRepository,
    private val context: Context
) : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> get() = _habits

    init {
        loadHabits()
    }

    fun scheduleHabitReset(habit: Habit) {
        val resetTime = when (habit.frequency) {
            "Daily" -> 24 * 60 * 60 * 1000L // 24 hours in milliseconds
            "Weekly" -> 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
            else -> 30 * 24 * 60 * 60 * 1000L // 30 days for Monthly
        }

        val workRequest = OneTimeWorkRequestBuilder<ResetHabitWorker>()
            .setInitialDelay(resetTime, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("habitId" to habit.id))
            .build()

        Log.d("WorkManager", "Scheduling reset in ${resetTime / 1000 / 60 / 60} hours")
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun loadHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val habitList = repository.getAllHabits()
                _habits.postValue(habitList)
            } catch (e: Exception) {
                _habits.postValue(emptyList())
            }
        }
    }
}
