package com.yousefwissam.dailyspark.ui.habit

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.main.MainActivity
import com.yousefwissam.dailyspark.ui.profile.ProfileActivity
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity
import com.yousefwissam.dailyspark.adapter.HabitAdapter
import com.yousefwissam.dailyspark.data.model.Habit

class EditHabitActivity : AppCompatActivity() {

    private lateinit var recyclerViewEdit: RecyclerView
    private lateinit var editHabitNameInput: EditText
    private lateinit var spinnerFrequency: Spinner
    private lateinit var saveEditButton: Button
    private lateinit var deleteHabitButton: Button

    private lateinit var habitAdapter: HabitAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var selectedHabitId: String? = null // Stores the selected habit's ID

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habit)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up the custom title TextView for the Toolbar
        val titleTextView = TextView(this)
        titleTextView.text = "DailySpark"
        titleTextView.textSize = 24f // Increase text size
        titleTextView.setTypeface(null, Typeface.BOLD) // Make it bold
        titleTextView.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor)) // Set text color
        titleTextView.layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER // Center the text in the toolbar
        }

        // Remove any default title and add the custom TextView
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.addView(titleTextView)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Set up ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main_menu -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.nav_add_habit -> {
                    startActivity(Intent(this, AddHabitActivity::class.java))
                }
                R.id.nav_edit_habit -> {
                    startActivity(Intent(this, EditHabitActivity::class.java))
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Initialize UI components
        recyclerViewEdit = findViewById(R.id.recyclerViewEdit)
        editHabitNameInput = findViewById(R.id.editHabitNameInput)
        spinnerFrequency = findViewById(R.id.spinnerFrequency)
        saveEditButton = findViewById(R.id.saveEditButton)
        deleteHabitButton = findViewById(R.id.buttonDeleteHabit)

        recyclerViewEdit.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(mutableListOf()) { habit ->
            loadHabitForEditing(habit) // Load habit details when user clicks for editing
        }
        recyclerViewEdit.adapter = habitAdapter

        // Set up spinner for frequency options
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.frequency_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequency.adapter = adapter

        // Load all habits into RecyclerView
        loadUserHabits()

        // Save edited habit details
        saveEditButton.setOnClickListener {
            saveEditedHabit()
        }

        // Delete selected habit
        deleteHabitButton.setOnClickListener {
            deleteSelectedHabit()
        }
    }

    // Load only the current user's habits from Firestore to display in RecyclerView
    private fun loadUserHabits() {
        val currentUser = auth.currentUser
        currentUser?.let {
            db.collection("habits")
                .whereEqualTo("userId", it.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val habits = documents.map { document ->
                        Habit(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            frequency = document.getString("frequency") ?: "",
                            createdDate = document.getLong("createdDate") ?: 0,
                            userId = it.uid
                        )
                    }
                    habitAdapter.updateData(habits)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading habits", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Load selected habit's details into the input fields for editing
    private fun loadHabitForEditing(habit: Habit) {
        selectedHabitId = habit.id
        editHabitNameInput.setText(habit.name)

        // Set spinner selection for frequency
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.frequency_options,
            android.R.layout.simple_spinner_item
        )
        val frequencyIndex = adapter.getPosition(habit.frequency)
        spinnerFrequency.setSelection(frequencyIndex)
    }

    // Save the edited habit details back to Firestore
    private fun saveEditedHabit() {
        val name = editHabitNameInput.text.toString().trim()
        val frequency = spinnerFrequency.selectedItem.toString()

        if (name.isNotEmpty() && frequency.isNotEmpty() && selectedHabitId != null) {
            val updatedHabit = mapOf(
                "name" to name,
                "frequency" to frequency
            )
            db.collection("habits").document(selectedHabitId!!)
                .update(updatedHabit)
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit updated successfully", Toast.LENGTH_SHORT).show()
                    loadUserHabits()
                    clearInputFields()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error updating habit", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select a habit and fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete the selected habit and its associated goals from Firestore
    private fun deleteSelectedHabit() {
        selectedHabitId?.let { habitId ->
            db.collection("habits").document(habitId).delete()
                .addOnSuccessListener {
                    // Delete associated goals
                    db.collection("goals").document(auth.currentUser!!.uid).collection("userGoals")
                        .whereEqualTo("habitId", habitId)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                db.collection("goals").document(auth.currentUser!!.uid)
                                    .collection("userGoals")
                                    .document(document.id)
                                    .delete()
                            }
                        }
                    Toast.makeText(this, "Habit and associated goals deleted successfully", Toast.LENGTH_SHORT).show()
                    loadUserHabits()
                    clearInputFields()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error deleting habit", Toast.LENGTH_SHORT).show()
                }
        } ?: Toast.makeText(this, "Please select a habit to delete", Toast.LENGTH_SHORT).show()
    }

    // Clear input fields after editing or deleting a habit
    private fun clearInputFields() {
        editHabitNameInput.text.clear()
        spinnerFrequency.setSelection(0)
        selectedHabitId = null
    }
}
