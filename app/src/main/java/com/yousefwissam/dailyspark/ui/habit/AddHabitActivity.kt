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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.main.MainActivity
import com.yousefwissam.dailyspark.ui.profile.ProfileActivity
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity
import com.yousefwissam.dailyspark.data.model.Habit

class AddHabitActivity : AppCompatActivity() {

    private lateinit var habitNameInput: EditText
    private lateinit var saveHabitButton: Button
    private lateinit var spinnerFrequency: Spinner
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

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

        // Initialize views
        habitNameInput = findViewById(R.id.habitNameInput)
        spinnerFrequency = findViewById(R.id.spinnerFrequency)
        saveHabitButton = findViewById(R.id.saveHabitButton)

        // Set up spinner for frequency options
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.frequency_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequency.adapter = adapter

        saveHabitButton.setOnClickListener {
            val habitName = habitNameInput.text.toString().trim()
            val habitFrequency = spinnerFrequency.selectedItem.toString()

            if (habitName.isNotEmpty() && habitFrequency.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val newHabit = Habit(
                        name = habitName,
                        frequency = habitFrequency,
                        createdDate = System.currentTimeMillis(),
                        userId = userId // Include the user ID
                    )
                    saveHabitToFirestore(newHabit)
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveHabitToFirestore(habit: Habit) {
        val currentUser = auth.currentUser
        currentUser?.let {
            val habitWithUserId = habit.copy(userId = it.uid)
            db.collection("habits")
                .add(habitWithUserId)
                .addOnSuccessListener {
                    Toast.makeText(this, "Habit added successfully", Toast.LENGTH_SHORT).show()
                    finish() // Go back to the main menu after successfully adding the habit
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error adding habit", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
