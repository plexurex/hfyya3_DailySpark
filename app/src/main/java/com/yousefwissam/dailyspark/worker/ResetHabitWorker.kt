package com.yousefwissam.dailyspark.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetHabitWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    val habitRepository = HabitRepository(FirebaseFirestore.getInstance())
// Assuming no arguments are needed for the constructor

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val habitId = inputData.getString("habitId") ?: return@withContext Result.failure()

        // Fetch the habit by ID and reset its completion status
        val habit = habitRepository.getHabitById(habitId)
        return@withContext if (habit != null) {
            habit.completed = false
            // Call insertOrUpdate instead of updateHabit
            habitRepository.insertOrUpdate(habit)
            Result.success()
        } else {
            Result.failure() // Return failure if habit is not found
        }
    }
}
