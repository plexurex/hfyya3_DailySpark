package com.yousefwissam.dailyspark.ui.habit

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
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
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity
import com.yousefwissam.dailyspark.data.model.Habit
import com.yousefwissam.dailyspark.adapter.HabitAdapter

class EditHabitDetailsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerViewEdit: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private var selectedHabitId: String? = null // Stores the selected habit's ID

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
            }
            drawerLayout.closeDrawers()
            true
        }

        // Set up RecyclerView
        recyclerViewEdit = findViewById(R.id.recyclerViewEdit)
        recyclerViewEdit.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(mutableListOf()) { habit -> loadHabitForEditing(habit) }
        recyclerViewEdit.adapter = habitAdapter

        // Load all habits into RecyclerView
        loadAllHabits()
    }

    private fun loadAllHabits() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("habits")
                .whereEqualTo("userId", currentUser.uid) // Filter habits by the current user's ID
                .get()
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
                    // Handle the error
                }
        }
    }

    private fun loadHabitForEditing(habit: Habit) {
        selectedHabitId = habit.id
        // Populate the edit text fields with selected habit's details
        findViewById<EditText>(R.id.editHabitNameInput).setText(habit.name)
        findViewById<EditText>(R.id.editHabitFrequencyInput).setText(habit.frequency)
    }
}
