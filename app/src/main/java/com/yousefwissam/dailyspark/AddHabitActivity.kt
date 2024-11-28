package com.yousefwissam.dailyspark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.data.Habit

class AddHabitActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

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
                val habit = Habit(
                    name = name,
                    frequency = frequency,
                    createdDate = System.currentTimeMillis()
                )

                db.collection("habits")
                    .add(habit)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Habit Added: $name", Toast.LENGTH_SHORT).show()
                        finish() // Close the activity after adding
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to add habit: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
