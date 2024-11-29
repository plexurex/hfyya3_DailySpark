package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.ui.EditHabitActivity

class AddHabitActivity : AppCompatActivity() {

    private lateinit var habitNameInput: EditText
    private lateinit var habitFrequencyInput: EditText
    private lateinit var saveHabitButton: Button
    private val db = FirebaseFirestore.getInstance()

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        // Set up the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
            }
            drawerLayout.closeDrawers()
            true
        }

        // Initialize views
        habitNameInput = findViewById(R.id.habitNameInput)
        habitFrequencyInput = findViewById(R.id.habitFrequencyInput)
        saveHabitButton = findViewById(R.id.saveHabitButton)

        saveHabitButton.setOnClickListener {
            val habitName = habitNameInput.text.toString().trim()
            val habitFrequency = habitFrequencyInput.text.toString().trim()

            if (habitName.isNotEmpty() && habitFrequency.isNotEmpty()) {
                val newHabit = Habit(name = habitName, frequency = habitFrequency, createdDate = System.currentTimeMillis())
                saveHabitToFirestore(newHabit)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveHabitToFirestore(habit: Habit) {
        db.collection("habits")
            .add(habit)
            .addOnSuccessListener {
                Toast.makeText(this, "Habit added successfully", Toast.LENGTH_SHORT).show()
                finish() // Go back to the main menu after successfully adding the habit
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error adding habit", Toast.LENGTH_SHORT).show()
            }
    }
}
