package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddHabitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        // Get references to input fields and button
        val habitNameInput = findViewById<EditText>(R.id.habitNameInput)
        val habitFrequencyInput = findViewById<EditText>(R.id.habitFrequencyInput)
        val addButton = findViewById<Button>(R.id.addButton)

        // Set up click listener for the button
        addButton.setOnClickListener {
            val name = habitNameInput.text.toString()
            val frequency = habitFrequencyInput.text.toString()

            if (name.isNotEmpty() && frequency.isNotEmpty()) {
                // Show a confirmation message (you can add functionality to save this habit later)
                Toast.makeText(this, "Habit Added: $name - $frequency", Toast.LENGTH_SHORT).show()
                finish() // Go back to the previous screen
            } else {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
