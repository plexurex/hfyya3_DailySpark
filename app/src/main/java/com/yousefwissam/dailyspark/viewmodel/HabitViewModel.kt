package com.yousefwissam.dailyspark.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.data.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> get() = _habits

    init {
        loadHabits()
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

    fun addHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("habits").add(habit)
                .addOnSuccessListener {
                    loadHabits() // Reload the habits to update the list
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            val habitMap = hashMapOf(
                "name" to habit.name,
                "frequency" to habit.frequency,
                "createdDate" to habit.createdDate,
                "completed" to habit.completed,
                "comment" to habit.comment
            )
            db.collection("habits").document(habit.id).set(habitMap)
                .addOnSuccessListener {
                    loadHabits() // Reload the habits to reflect the updated data
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    fun deleteHabit(habitId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            db.collection("habits").document(habitId).delete()
                .addOnSuccessListener {
                    loadHabits() // Reload the habits to remove the deleted one
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    fun markHabitAsCompleted(habit: Habit, comment: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val habitMap = hashMapOf(
                "name" to habit.name,
                "frequency" to habit.frequency,
                "createdDate" to habit.createdDate,
                "completed" to true,
                "comment" to comment
            )
            db.collection("habits").document(habit.id).set(habitMap)
                .addOnSuccessListener {
                    loadHabits() // Reload the habits to reflect the updated data
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }
}