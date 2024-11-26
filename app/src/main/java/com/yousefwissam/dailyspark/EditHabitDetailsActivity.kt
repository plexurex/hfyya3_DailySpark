package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel
import com.yousefwissam.dailyspark.viewmodel.HabitViewModelFactory
import com.yousefwissam.dailyspark.data.HabitDatabase
import com.yousefwissam.dailyspark.repository.HabitRepository

class EditHabitDetailsActivity : AppCompatActivity() {
    private lateinit var habitNameInput: EditText
    private lateinit var habitFrequencyInput: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button // Added delete button

    private val viewModel: HabitViewModel by viewModels {
        val database = HabitDatabase.getDatabase(application)
        val repository = HabitRepository(database.habitDao())
        HabitViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habit_details)

        habitNameInput = findViewById(R.id.editHabitNameInput)
        habitFrequencyInput = findViewById(R.id.editHabitFrequencyInput)
        saveButton = findViewById(R.id.saveEditButton)
        deleteButton = findViewById(R.id.buttonDeleteHabit) // Initialize delete button

        val habitId = intent.getIntExtra("HABIT_ID", -1)
        if (habitId != -1) {
            viewModel.getHabitById(habitId).observe(this) { habit ->
                if (habit != null) {
                    habitNameInput.setText(habit.name)
                    habitFrequencyInput.setText(habit.frequency)
                } else {
                    Toast.makeText(this, "Error loading habit", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Invalid habit ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        saveButton.setOnClickListener {
            val updatedName = habitNameInput.text.toString()
            val updatedFrequency = habitFrequencyInput.text.toString()

            if (updatedName.isNotEmpty() && updatedFrequency.isNotEmpty()) {
                val updatedHabit = com.yousefwissam.dailyspark.data.Habit(
                    id = habitId,
                    name = updatedName,
                    frequency = updatedFrequency,
                    createdDate = System.currentTimeMillis()
                )
                viewModel.updateHabit(updatedHabit)
                Toast.makeText(this, "Habit updated!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete button logic
        deleteButton.setOnClickListener {
            if (habitId != -1) {
                viewModel.deleteHabitById(habitId) // Call the ViewModel delete method
                Toast.makeText(this, "Habit deleted!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error deleting habit", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
