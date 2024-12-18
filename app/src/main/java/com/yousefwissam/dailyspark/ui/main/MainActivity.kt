package com.yousefwissam.dailyspark.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity.Companion.scheduleDailyNotification
import com.yousefwissam.dailyspark.data.model.Habit
import com.yousefwissam.dailyspark.ui.habit.EditHabitActivity
import com.yousefwissam.dailyspark.adapter.HabitAdapter
import com.yousefwissam.dailyspark.ui.habit.HabitDetailsActivity
import com.yousefwissam.dailyspark.ui.habit.AddHabitActivity
import com.yousefwissam.dailyspark.ui.profile.ProfileActivity
import com.yousefwissam.dailyspark.utils.NotificationUtils

class MainActivity : AppCompatActivity() {
    companion object {
        const val RC_SIGN_IN = 123
    }

    private val preferences: SharedPreferences by lazy {
        getSharedPreferences("com.yousefwissam.dailyspark.PREFERENCES", Context.MODE_PRIVATE)
    }

    private val quotes = listOf(
        "Stay positive and keep building your habits!",
        "Success is the sum of small efforts repeated day in and day out.",
        "You are what you repeatedly do. Excellence is not an act, but a habit.",
        "It's not about having time, it's about making time.",
        "Dream big. Start small. Act now.",
        "Your only limit is your mindset.",
        "Progress beats perfection every time.",
        "Turn obstacles into opportunities.",
        "Believe, achieve, repeat."
    )

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView
    lateinit var habitAdapter: HabitAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userId get() = auth.currentUser?.uid ?: "currentUser"

    override fun onResume() {
        super.onResume()
        loadAllHabits() // Load habits every time the Main Activity resumes
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase Authentication Check
        if (auth.currentUser == null) {
            startFirebaseSignIn()
        }

        // Create Notification Channel (if not already created)
        NotificationUtils.createNotificationChannel(this)

        // Check if notifications are enabled and schedule them if needed
        val isNotificationEnabled = preferences.getBoolean("NOTIFICATION_ENABLED", false)
        if (isNotificationEnabled) {
            scheduleDailyNotification(this)
        }

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
                // Handle navigation to main menu
                R.id.nav_main_menu -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                // Handle navigation to add habit
                R.id.nav_add_habit -> {
                    startActivity(Intent(this, AddHabitActivity::class.java))
                }
                // Handle navigation to edit habit
                R.id.nav_edit_habit -> {
                    startActivity(Intent(this, EditHabitActivity::class.java))
                }
                // Handle navigation to settings
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                // Handle navigation to profile
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
    // Firebase Authentication
    private fun startFirebaseSignIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }
    // Handle Firebase Authentication result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                Toast.makeText(this, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                loadAllHabits()
            } else {
                // Sign-in failed
                response?.error?.let {
                    Toast.makeText(this, "Sign-in failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    // Load all habits from Firestore
    fun loadAllHabits() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("habits")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val habits = documents.map { document ->
                        Habit(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            frequency = document.getString("frequency") ?: "",
                            createdDate = document.getLong("createdDate") ?: 0,
                            userId = document.getString("userId") ?: ""
                        )
                    }
                    habitAdapter.updateData(habits.toMutableList())
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading habits", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
    // Navigate to Habit Details
    private fun navigateToHabitDetails(habit: Habit) {
        val intent = Intent(this, HabitDetailsActivity::class.java)
        intent.putExtra("HABIT_ID", habit.id)
        startActivity(intent)
    }
}