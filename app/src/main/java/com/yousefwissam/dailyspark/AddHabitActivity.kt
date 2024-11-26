package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel
import com.yousefwissam.dailyspark.viewmodel.HabitViewModelFactory
import com.yousefwissam.dailyspark.data.HabitDatabase
import com.yousefwissam.dailyspark.repository.HabitRepository
import java.util.*

class AddHabitActivity : AppCompatActivity() {
    private val viewModel: HabitViewModel by viewModels {
        HabitViewModelFactory(HabitRepository(HabitDatabase.getDatabase(this).habitDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        val habitNameInput = findViewById<EditText>(R.id.habitNameInput)
        val habitFrequencyInput = findViewById<EditText>(R.id.habitFrequencyInput)
        val addButton = findViewById<Button>(R.id.addButton)

        addButton.setOnClickListener {
            val name = habitNameInput.text.toString().trim()
            val frequency = habitFrequencyInput.text.toString().trim()

            if (name.isNotEmpty() && frequency.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()  // Get current time as created date
                val newHabit = Habit(name = name, frequency = frequency, createdDate = currentTime)
                viewModel.insertHabit(newHabit)  // Use ViewModel to insert the new habit into the database
                Toast.makeText(this, "Habit Added: $name - $frequency", Toast.LENGTH_SHORT).show()
                finish() // Go back to the previous screen
            } else {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
