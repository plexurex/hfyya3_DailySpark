package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yousefwissam.dailyspark.data.Habit

class TrackHabitActivity : AppCompatActivity() {
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

        // Add functionality to log progress (for now, we'll just log a message)
        trackProgressButton.setOnClickListener {
            // Log habit progress (implement the logic later)
        }
    }
}
