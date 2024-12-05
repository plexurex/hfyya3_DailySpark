package com.yousefwissam.dailyspark.ui.habit

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.model.Habit

class TrackHabitActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_habit)

        // Get the Habit object passed from the Intent
        val habit = intent.getSerializableExtra("habit") as? Habit

        // References to UI elements
        val habitNameTextView: TextView = findViewById(R.id.habitNameTextView)
        val habitFrequencyTextView: TextView = findViewById(R.id.habitFrequencyTextView)
        val trackProgressButton: Button = findViewById(R.id.trackProgressButton)

        // Set habit details to the UI
        habit?.let {
            habitNameTextView.text = it.name
            habitFrequencyTextView.text = it.frequency
        }

        // Add functionality to log progress
        trackProgressButton.setOnClickListener {
            habit?.let {
                trackHabitProgress(it)
            }
        }
    }

    private fun trackHabitProgress(habit: Habit) {
        val habitRef = db.collection("habits").document(habit.id)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(habitRef)
            val newStreak = (snapshot.getLong("streak") ?: 0) + 1
            transaction.update(habitRef, "streak", newStreak)
        }.addOnSuccessListener {
            Toast.makeText(this, "Habit progress tracked!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error tracking progress: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}