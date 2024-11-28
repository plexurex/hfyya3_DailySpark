package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.data.Habit

class HabitDetailsActivity : AppCompatActivity() {

    private lateinit var habitNameTextView: TextView
    private lateinit var habitFrequencyTextView: TextView
    private lateinit var completedCheckbox: CheckBox
    private lateinit var commentEditText: EditText
    private lateinit var saveButton: Button

    private val db = FirebaseFirestore.getInstance()
    private var habitId: String? = null

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
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating habit", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Invalid habit ID", Toast.LENGTH_SHORT).show()
        }
    }
}
