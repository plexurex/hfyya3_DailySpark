package com.yousefwissam.dailyspark.ui.settings

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.notifications.DailyNotificationWorker
import com.yousefwissam.dailyspark.ui.AuthenticationActivity
import com.yousefwissam.dailyspark.ui.habit.EditHabitActivity
import com.yousefwissam.dailyspark.ui.habit.AddHabitActivity
import com.yousefwissam.dailyspark.ui.main.MainActivity
import com.yousefwissam.dailyspark.ui.profile.ProfileActivity
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

    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    val preferences: SharedPreferences by lazy {
        getSharedPreferences("com.yousefwissam.dailyspark.PREFERENCES", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (!isRunningInTest()) {
            initializeFirebase()
        }

        NotificationUtils.createNotificationChannel(this)
        setupToolbar()
        setupNavigation()
        initializeUIComponents()

        val isNotificationEnabled = preferences.getBoolean("NOTIFICATION_ENABLED", false)
        notificationSwitch.isChecked = isNotificationEnabled

        setButtonListeners()
    }
    // Initialize Firebase
    private fun initializeFirebase() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    // Check if the test is running
    private fun isRunningInTest(): Boolean {
        return try {
            Class.forName("org.robolectric.Robolectric")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
    // Set up the toolbar
    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val titleTextView = TextView(this)
        titleTextView.text = "DailySpark"
        titleTextView.textSize = 24f
        titleTextView.setTypeface(null, Typeface.BOLD)
        titleTextView.setTextColor(ContextCompat.getColor(this, R.color.lightTextColor))
        titleTextView.layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.addView(titleTextView)
    }
    // Set up navigation
    private fun setupNavigation() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                // Handle navigation to main menu
                R.id.nav_main_menu -> startActivity(Intent(this, MainActivity::class.java))
                // Handle navigation to add habit
                R.id.nav_add_habit -> startActivity(Intent(this, AddHabitActivity::class.java))
                // Handle navigation to edit habit
                R.id.nav_edit_habit -> startActivity(Intent(this, EditHabitActivity::class.java))
                // Handle navigation to settings
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                // Handle navigation to profile
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
    // Set up button listeners
    private fun setButtonListeners() {
        deleteDataButton.setOnClickListener {
            deleteAllHabits()
        }

        deleteGoalsButton.setOnClickListener {
            deleteAllGoals()
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Ask for user permission to enable notifications
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Enable Notifications")
                    .setMessage("Would you like to receive daily motivational notifications?")
                    .setPositiveButton("Yes") { _, _ ->
                        preferences.edit().putBoolean("NOTIFICATION_ENABLED", true).apply()
                        scheduleDailyNotification(this)
                        Toast.makeText(this, "Daily notifications enabled", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { _, _ ->
                        notificationSwitch.isChecked = false
                    }
                    .create()
                dialog.show()
            } else {
                preferences.edit().putBoolean("NOTIFICATION_ENABLED", false).apply()
                cancelDailyNotification()
                Toast.makeText(this, "Daily notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Delete all habits and associated goals
    private fun deleteAllHabits() {
        db.collection("habits").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val habitId = document.id

                    db.collection("habits").document(habitId).delete()

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
    // Delete all goals
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
    // Logout user
    fun logoutUser() {
        auth.signOut()
        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
    // Schedule daily notification
    companion object {
        fun scheduleDailyNotification(context: Context) {
            val currentTime = System.currentTimeMillis()
            val dailyTriggerTime = Calendar.getInstance().apply {
                timeInMillis = currentTime
                set(Calendar.HOUR_OF_DAY, 9)
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
    // Cancel daily notification
    private fun cancelDailyNotification() {
        WorkManager.getInstance(this).cancelUniqueWork("DailyNotificationWork")
        Toast.makeText(this, "Daily notification cancelled", Toast.LENGTH_SHORT).show()
    }
}
