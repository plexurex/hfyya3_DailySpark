package com.yousefwissam.dailyspark.ui

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.worker.ResetHabitWorker
import java.util.concurrent.TimeUnit

class HabitDetailsActivity : AppCompatActivity() {

    private lateinit var habitNameTextView: TextView
    private lateinit var habitFrequencyTextView: TextView
    private lateinit var completedCheckbox: CheckBox
    private lateinit var commentEditText: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var habitId: String? = null
    private val userId = "currentUser" // Assuming you have a way to identify the current user
    private var lastCompletionTime: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_details)

        habitNameTextView = findViewById(R.id.textViewHabitName)
        habitFrequencyTextView = findViewById(R.id.textViewHabitFrequency)
        completedCheckbox = findViewById(R.id.checkBoxCompleted)
        commentEditText = findViewById(R.id.editTextComment)
        saveButton = findViewById(R.id.buttonSave)

        habitId = intent.getStringExtra("HABIT_ID")

        if (habitId != null) {
            getHabitById(habitId!!)
        } else {
            Toast.makeText(this, "Invalid habit ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        saveButton.setOnClickListener {
            saveHabitDetails()
        }
    }

    private fun getHabitById(habitId: String) {
        db.collection("habits").document(habitId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val habit = document.toObject(Habit::class.java)
                    if (habit != null) {
                        habitNameTextView.text = habit.name
                        habitFrequencyTextView.text = habit.frequency
                        completedCheckbox.isChecked = habit.completed
                        commentEditText.setText(habit.comment)

                        lastCompletionTime = document.getLong("lastCompletionTime") ?: 0L
                        val currentTime = System.currentTimeMillis()

                        // Check if the habit can be completed based on frequency
                        val nextAvailableTime = when (habit.frequency) {
                            "Daily" -> lastCompletionTime + TimeUnit.DAYS.toMillis(1)
                            "Weekly" -> lastCompletionTime + TimeUnit.DAYS.toMillis(7)
                            "Monthly" -> lastCompletionTime + TimeUnit.DAYS.toMillis(30)
                            else -> 0L
                        }

                        if (currentTime < nextAvailableTime) {
                            completedCheckbox.isEnabled = false
                        } else {
                            completedCheckbox.isEnabled = true
                        }

                        // Schedule reset based on frequency if already completed
                        if (habit.completed) {
                            scheduleHabitReset(habit)
                        }
                    } else {
                        Toast.makeText(this, "Error loading habit", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Habit not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading habit: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun saveHabitDetails() {
        val completed = completedCheckbox.isChecked
        val comment = commentEditText.text.toString()
        val currentTime = System.currentTimeMillis()

        if (habitId != null) {
            // Calculate the next available completion time based on frequency
            val nextAvailableTime = when (habitFrequencyTextView.text.toString()) {
                "Daily" -> lastCompletionTime + TimeUnit.DAYS.toMillis(1)
                "Weekly" -> lastCompletionTime + TimeUnit.DAYS.toMillis(7)
                "Monthly" -> lastCompletionTime + TimeUnit.DAYS.toMillis(30)
                else -> 0L
            }

            if (completed && currentTime < nextAvailableTime) {
                // If user tries to complete before reset, show a message and prevent save
                Toast.makeText(this, "You need to wait before completing this habit again.", Toast.LENGTH_SHORT).show()
                return
            }

            val updates = mapOf(
                "completed" to completed,
                "comment" to comment,
                "lastCompletionTime" to if (completed) currentTime else null
            )
            db.collection("habits").document(habitId!!).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit updated successfully", Toast.LENGTH_SHORT).show()
                    // Update points if the habit is completed
                    if (completed) {
                        updatePoints(10) // Assuming 10 points per habit completion
                        completedCheckbox.isEnabled = false // Disable checkbox until reset
                        scheduleHabitReset(getCurrentHabit())
                    }
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating habit", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid habit ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePoints(points: Int) {
        val userRewardsDoc = db.collection("rewards").document(userId)

        userRewardsDoc.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPoints = document.getLong("points") ?: 0
                    val newPoints = currentPoints + points
                    userRewardsDoc.update("points", newPoints)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Points updated! Total: $newPoints", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error updating points", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // If record does not exist, create it
                    val newReward = mapOf(
                        "badgeName" to "Initial Badge",
                        "points" to points
                    )
                    userRewardsDoc.set(newReward)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Points awarded!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error awarding points", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching reward data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scheduleHabitReset(habit: Habit) {
        val resetTime = when (habit.frequency) {
            "Daily" -> 24 * 60 * 60 * 1000L // 24 hours in milliseconds
            "Weekly" -> 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
            "Monthly" -> 30 * 24 * 60 * 60 * 1000L // 30 days in milliseconds
            else -> 0L
        }

        if (resetTime > 0) {
            val workRequest: WorkRequest = OneTimeWorkRequestBuilder<ResetHabitWorker>()
                .setInitialDelay(resetTime, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf("habitId" to habitId.toString()))
                .build()

            WorkManager.getInstance(applicationContext).enqueue(workRequest)
        }
    }

    private fun getCurrentHabit(): Habit {
        return Habit(
            id = habitId!!,
            name = habitNameTextView.text.toString(),
            frequency = habitFrequencyTextView.text.toString(),
            completed = completedCheckbox.isChecked,
            comment = commentEditText.text.toString()
        )
    }
}
