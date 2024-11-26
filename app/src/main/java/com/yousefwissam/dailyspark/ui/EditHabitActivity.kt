package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.data.HabitDatabase
import com.yousefwissam.dailyspark.repository.HabitRepository
import com.yousefwissam.dailyspark.ui.HabitAdapter
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel
import com.yousefwissam.dailyspark.viewmodel.HabitViewModelFactory

class EditHabitActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var editHabitNameInput: EditText
    private lateinit var editHabitFrequencyInput: EditText
    private lateinit var saveEditButton: Button
    private lateinit var deleteHabitButton: Button

    private lateinit var habitAdapter: HabitAdapter
    private var selectedHabitId: Int? = null // Track the selected habit's ID

    private val viewModel: HabitViewModel by viewModels {
        val database = HabitDatabase.getDatabase(application)
        val repository = HabitRepository(database.habitDao())
        HabitViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habit)

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerViewEdit)
        editHabitNameInput = findViewById(R.id.editHabitNameInput)
        editHabitFrequencyInput = findViewById(R.id.editHabitFrequencyInput)
        saveEditButton = findViewById(R.id.saveEditButton)
        deleteHabitButton = findViewById(R.id.buttonDeleteHabit)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize HabitAdapter with viewModel for click handling
        habitAdapter = HabitAdapter(
            mutableListOf(),
            viewModel
        ) { habit -> loadHabitForEditing(habit) } // Pass lambda for habit selection
        recyclerView.adapter = habitAdapter

        // Observe habits and update the adapter
        viewModel.allHabits.observe(this) { habits ->
            if (habits != null) {
                habitAdapter.updateData(habits)
            } else {
                Toast.makeText(this, "No habits found", Toast.LENGTH_SHORT).show()
            }
        }

        // Save edited habit
        saveEditButton.setOnClickListener {
            val name = editHabitNameInput.text.toString()
            val frequency = editHabitFrequencyInput.text.toString()

            if (name.isNotEmpty() && frequency.isNotEmpty()) {
                val habit = Habit(
                    id = selectedHabitId ?: 0, // Use the selected habit's ID
                    name = name,
                    frequency = frequency,
                    createdDate = System.currentTimeMillis() // Keep created date consistent
                )
                viewModel.updateHabit(habit)
                Toast.makeText(this, "Habit updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete the selected habit
        deleteHabitButton.setOnClickListener {
            val selectedId = selectedHabitId
            if (selectedId != null) {
                viewModel.deleteHabitById(selectedId)
                Toast.makeText(this, "Habit deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "No habit selected to delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadHabitForEditing(habit: Habit) {
        // Populate fields with selected habit's data
        editHabitNameInput.setText(habit.name)
        editHabitFrequencyInput.setText(habit.frequency)
        selectedHabitId = habit.id // Mark this habit as selected
    }
}
