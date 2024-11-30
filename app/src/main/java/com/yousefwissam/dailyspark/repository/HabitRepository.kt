package com.yousefwissam.dailyspark.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.yousefwissam.dailyspark.data.Habit
import kotlinx.coroutines.tasks.await

class HabitRepository {

    private val db = FirebaseFirestore.getInstance()
    private val habitsCollection = db.collection("habits")

    suspend fun markHabitAsCompleted(habit: Habit) {
        val updatedHabit = habit.copy(
            completed = true,
            currentStreak = habit.currentStreak + 1
        )
        insertOrUpdate(updatedHabit)
    }


    // Fetch all habits
    suspend fun getAllHabits(): List<Habit> {
        return try {
            val snapshot = habitsCollection.get().await()
            snapshot.toObjects(Habit::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Fetch a specific habit by ID
    suspend fun getHabitById(id: String): Habit? {
        return try {
            val snapshot = habitsCollection.document(id).get().await()
            snapshot.toObject()
        } catch (e: Exception) {
            null
        }
    }

    // Insert or update a habit
    suspend fun insertOrUpdate(habit: Habit) {
        try {
            habitsCollection.document(habit.id).set(habit).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    // Delete a habit by ID
    suspend fun deleteHabitById(id: String) {
        try {
            habitsCollection.document(id).delete().await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    // Delete all habits
    suspend fun deleteAll() {
        try {
            val snapshot = habitsCollection.get().await()
            for (document in snapshot.documents) {
                habitsCollection.document(document.id).delete().await()
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}