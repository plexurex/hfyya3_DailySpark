package com.yousefwissam.dailyspark.ui

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.Habit

class HabitDetailsActivity : AppCompatActivity() {

    private lateinit var habitNameTextView: TextView
    private lateinit var habitFrequencyTextView: TextView
    private lateinit var completedCheckbox: CheckBox
    private lateinit var commentEditText: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var habitId: String? = null
    private val userId = "currentUser" // Assuming you have a way to identify the current user

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

        if (habitId != null) {
            val updates = mapOf(
                "completed" to completed,
                "comment" to comment
            )
            db.collection("habits").document(habitId!!).update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit updated successfully", Toast.LENGTH_SHORT).show()
                    // Update points if the habit is completed
                    if (completed) {
                        updatePoints(10) // Assuming 10 points per habit completion
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
}
