package com.yousefwissam.dailyspark.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.Habit

class EditHabitActivity : AppCompatActivity() {

    private lateinit var recyclerViewEdit: RecyclerView
    private lateinit var editHabitNameInput: EditText
    private lateinit var editHabitFrequencyInput: EditText
    private lateinit var saveEditButton: Button
    private lateinit var deleteHabitButton: Button

    private lateinit var habitAdapter: HabitAdapter
    private val db = FirebaseFirestore.getInstance()
    private var selectedHabitId: String? = null // Stores the selected habit's ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habit)

        // Initialize UI components
        recyclerViewEdit = findViewById(R.id.recyclerViewEdit)
        editHabitNameInput = findViewById(R.id.editHabitNameInput)
        editHabitFrequencyInput = findViewById(R.id.editHabitFrequencyInput)
        saveEditButton = findViewById(R.id.saveEditButton)
        deleteHabitButton = findViewById(R.id.buttonDeleteHabit)

        recyclerViewEdit.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(mutableListOf()) { habit ->
            loadHabitForEditing(habit) // Load habit details when user clicks for editing
        }
        recyclerViewEdit.adapter = habitAdapter

        // Load all habits into RecyclerView
        loadAllHabits()

        // Save edited habit details
        saveEditButton.setOnClickListener {
            saveEditedHabit()
        }

        // Delete selected habit
        deleteHabitButton.setOnClickListener {
            deleteSelectedHabit()
        }
    }

    // Load all habits from Firestore to display in RecyclerView
    private fun loadAllHabits() {
        db.collection("habits").get()
            .addOnSuccessListener { documents ->
                val habits = documents.map { document ->
                    Habit(
                        id = document.id,
                        name = document.getString("name") ?: "",
                        frequency = document.getString("frequency") ?: "",
                        createdDate = document.getLong("createdDate") ?: 0
                    )
                }
                habitAdapter.updateData(habits)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading habits", Toast.LENGTH_SHORT).show()
            }
    }

    // Load selected habit's details into the input fields for editing
    private fun loadHabitForEditing(habit: Habit) {
        selectedHabitId = habit.id
        editHabitNameInput.setText(habit.name)
        editHabitFrequencyInput.setText(habit.frequency)
    }

    // Save the edited habit details back to Firestore
    private fun saveEditedHabit() {
        val name = editHabitNameInput.text.toString()
        val frequency = editHabitFrequencyInput.text.toString()

        if (name.isNotEmpty() && frequency.isNotEmpty() && selectedHabitId != null) {
            val updatedHabit = mapOf(
                "name" to name,
                "frequency" to frequency
            )
            db.collection("habits").document(selectedHabitId!!).update(updatedHabit)
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit updated successfully", Toast.LENGTH_SHORT).show()
                    // Refresh the list after update
                    loadAllHabits()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating habit", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select a habit and fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete the selected habit from Firestore
    private fun deleteSelectedHabit() {
        selectedHabitId?.let {
            db.collection("habits").document(it).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit deleted successfully", Toast.LENGTH_SHORT).show()
                    // Clear input fields and refresh list
                    editHabitNameInput.text.clear()
                    editHabitFrequencyInput.text.clear()
                    selectedHabitId = null
                    loadAllHabits()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting habit", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "Please select a habit to delete", Toast.LENGTH_SHORT).show()
    }
}
