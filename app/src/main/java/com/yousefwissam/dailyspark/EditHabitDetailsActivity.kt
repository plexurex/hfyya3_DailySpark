package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.data.Habit

class EditHabitDetailsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var habitNameInput: EditText
    private lateinit var habitFrequencyInput: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private var habitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habit_details)

        habitNameInput = findViewById(R.id.editHabitNameInput)
        habitFrequencyInput = findViewById(R.id.editHabitFrequencyInput)
        saveButton = findViewById(R.id.saveEditButton)
        deleteButton = findViewById(R.id.buttonDeleteHabit)

        habitId = intent.getStringExtra("HABIT_ID")

        // Load the habit data from Firestore
        habitId?.let { id ->
            db.collection("habits").document(id).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val habit = document.toObject(Habit::class.java)
                        habit?.let {
                            habitNameInput.setText(it.name)
                            habitFrequencyInput.setText(it.frequency)
                        }
                    } else {
                        Toast.makeText(this, "Error loading habit", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading habit", Toast.LENGTH_SHORT).show()
                    finish()
                }
        } ?: run {
            Toast.makeText(this, "Invalid habit ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Save edited habit
        saveButton.setOnClickListener {
            val updatedName = habitNameInput.text.toString()
            val updatedFrequency = habitFrequencyInput.text.toString()

            if (updatedName.isNotEmpty() && updatedFrequency.isNotEmpty()) {
                val updatedHabit = Habit(
                    id = habitId ?: "",
                    name = updatedName,
                    frequency = updatedFrequency,
                    createdDate = System.currentTimeMillis()
                )
                updateHabitInFirestore(updatedHabit)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete the selected habit
        deleteButton.setOnClickListener {
            habitId?.let { id ->
                deleteHabitFromFirestore(id)
            }
        }
    }

    private fun updateHabitInFirestore(habit: Habit) {
        db.collection("habits").document(habit.id).set(habit)
            .addOnSuccessListener {
                Toast.makeText(this, "Habit updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating habit: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteHabitFromFirestore(id: String) {
        db.collection("habits").document(id).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Habit deleted!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting habit: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
