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
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.worker.ResetHabitWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class HabitViewModel(private val context: Context) : ViewModel() {  // Context is passed in during ViewModel creation

    private val db = FirebaseFirestore.getInstance()
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

    private fun loadHabits() {
        db.collection("habits").get()
            .addOnSuccessListener { result ->
                val habitList = result.map { document ->
                    Habit(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        frequency = document.getString("frequency") ?: "",
                        createdDate = document.getLong("createdDate") ?: 0,
                        completed = document.getBoolean("completed") ?: false,
                        comment = document.getString("comment") ?: ""
                    )
                }
                _habits.value = habitList
            }
            .addOnFailureListener {
                _habits.value = emptyList()
            }
    }
}
