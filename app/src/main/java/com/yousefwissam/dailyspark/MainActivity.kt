package com.yousefwissam.dailyspark

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.EditHabitActivity
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.ui.HabitAdapter
import com.yousefwissam.dailyspark.ui.HabitDetailsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onResume() {
        super.onResume()
        loadAllHabits() // Load habits every time the Main Activity resumes
    }
    private val quotes = listOf(
        "Stay positive and keep building your habits!",
        "Success is the sum of small efforts repeated day in and day out.",
        "You are what you repeatedly do. Excellence is not an act, but a habit.",
        "It's not about having time, it's about making time."
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize buttons
        val addHabitButton: Button = findViewById(R.id.addHabitButton)
        val editHabitButton: Button = findViewById(R.id.editHabitButton)

        addHabitButton.setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
        }

        editHabitButton.setOnClickListener {
            startActivity(Intent(this, EditHabitActivity::class.java))
        }

        // Set a random motivational quote
        val quoteTextView: TextView = findViewById(R.id.textViewQuote)
        val randomQuote = quotes.random()
        quoteTextView.text = randomQuote



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

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(mutableListOf()) { habit ->
            navigateToHabitDetails(habit)
        }
        recyclerView.adapter = habitAdapter

        // Load habits
        loadAllHabits()
    }

    private fun loadAllHabits() {
        val db = FirebaseFirestore.getInstance()
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
                habitAdapter.updateData(habits.toMutableList())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading habits", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHabitDetails(habit: Habit) {
        val intent = Intent(this, HabitDetailsActivity::class.java)
        intent.putExtra("HABIT_ID", habit.id)
        startActivity(intent)
    }
}