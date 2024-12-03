package com.yousefwissam.dailyspark

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.notifications.DailyNotificationWorker
import com.yousefwissam.dailyspark.ui.AuthenticationActivity
import com.yousefwissam.dailyspark.ui.EditHabitActivity
import com.yousefwissam.dailyspark.utils.NotificationUtils
import java.util.*
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var deleteDataButton: Button
    private lateinit var deleteGoalsButton: Button
    private lateinit var notificationSwitch: Switch
    private lateinit var logoutButton: Button

    // FirebaseFirestore and FirebaseAuth instances, marked as lateinit for injection during testing
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    // SharedPreferences for saving notification switch state
    val preferences: SharedPreferences by lazy {
        getSharedPreferences("com.yousefwissam.dailyspark.PREFERENCES", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Initialize Firebase instances only if not running in a test environment
        if (!isRunningInTest()) {
            initializeFirebase()
        }

        // Create Notification Channel
        NotificationUtils.createNotificationChannel(this)

        // Set up the Toolbar
        setupToolbar()

        // Initialize DrawerLayout and NavigationView
        setupNavigation()

        // Initialize UI components
        initializeUIComponents()

        // Load the saved notification switch state
        val isNotificationEnabled = preferences.getBoolean("NOTIFICATION_ENABLED", false)
        notificationSwitch.isChecked = isNotificationEnabled

        // Set click listeners for buttons
        setButtonListeners()
    }

    // Initialize Firebase instances
    private fun initializeFirebase() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    // Method to inject dependencies for testing purposes
    fun injectDependencies(auth: FirebaseAuth, db: FirebaseFirestore) {
        this.auth = auth
        this.db = db
    }

    // Utility function to determine if we're running in a test environment
    private fun isRunningInTest(): Boolean {
        return try {
            Class.forName("org.robolectric.Robolectric")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    // Set up the Toolbar
    private fun setupToolbar() {
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
    }

    // Set up DrawerLayout and NavigationView
    private fun setupNavigation() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Set up ActionBarDrawerToggle
        val toolbar: Toolbar = findViewById(R.id.toolbar)
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
                R.id.nav_main_menu -> startActivity(Intent(this, MainActivity::class.java))
                R.id.nav_add_habit -> startActivity(Intent(this, AddHabitActivity::class.java))
                R.id.nav_edit_habit -> startActivity(Intent(this, EditHabitActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Initialize UI components
    private fun initializeUIComponents() {
        deleteDataButton = findViewById(R.id.deleteDataButton)
        deleteGoalsButton = findViewById(R.id.deleteGoalsButton)
        notificationSwitch = findViewById(R.id.notificationSwitch)
        logoutButton = findViewById(R.id.logoutButton)
    }

    // Set button listeners
    private fun setButtonListeners() {
        // Set click listener to delete all habits and associated goals
        deleteDataButton.setOnClickListener {
            deleteAllHabits()
        }

        // Set click listener to delete all goals
        deleteGoalsButton.setOnClickListener {
            deleteAllGoals()
        }

        // Set click listener for logout button
        logoutButton.setOnClickListener {
            logoutUser()
        }

        // Set listener for notification switch
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("NOTIFICATION_ENABLED", isChecked).apply()
            if (isChecked) {
                scheduleDailyNotification(this)
                Toast.makeText(this, "Daily notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                cancelDailyNotification()
                Toast.makeText(this, "Daily notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delete all habits and their associated goals from Firestore
    private fun deleteAllHabits() {
        db.collection("habits").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val habitId = document.id

                    // Delete the habit itself
                    db.collection("habits").document(habitId).delete()

                    // Delete associated goals
                    db.collection("goals").document("currentUser").collection("userGoals")
                        .whereEqualTo("habitId", habitId)
                        .get()
                        .addOnSuccessListener { goalDocuments ->
                            for (goalDocument in goalDocuments) {
                                db.collection("goals").document("currentUser")
                                    .collection("userGoals")
                                    .document(goalDocument.id)
                                    .delete()
                            }
                        }
                }
                Toast.makeText(this, "All habits and associated goals deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error deleting all habits", Toast.LENGTH_SHORT).show()
            }
    }

    // Delete all goals from Firestore
    private fun deleteAllGoals() {
        db.collection("goals").document("currentUser").collection("userGoals").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("goals").document("currentUser")
                        .collection("userGoals")
                        .document(document.id)
                        .delete()
                }
                Toast.makeText(this, "All goals deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error deleting all goals", Toast.LENGTH_SHORT).show()
            }
    }

    // Log the user out of the application
    fun logoutUser() {
        auth.signOut()
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish() // Close the settings activity to prevent going back to it
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    // Schedule a daily notification using AlarmManager and WorkManager
    companion object {
        fun scheduleDailyNotification(context: Context) {
            val currentTime = System.currentTimeMillis()
            val dailyTriggerTime = Calendar.getInstance().apply {
                timeInMillis = currentTime
                set(Calendar.HOUR_OF_DAY, 9)  // Schedule for 9 AM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }.timeInMillis
            val delay = dailyTriggerTime - currentTime

            val dailyWorkRequest = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("daily_notification_work")
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    "DailyNotificationWork",
                    ExistingWorkPolicy.REPLACE,
                    dailyWorkRequest
                )

            Toast.makeText(context, "Daily notification scheduled", Toast.LENGTH_SHORT).show()
        }
    }

    // Cancel the daily notification
    private fun cancelDailyNotification() {
        WorkManager.getInstance(this).cancelUniqueWork("DailyNotificationWork")
        Toast.makeText(this, "Daily notification cancelled", Toast.LENGTH_SHORT).show()
    }
}
