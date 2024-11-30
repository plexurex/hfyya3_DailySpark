package com.yousefwissam.dailyspark

import android.content.Intent
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.EditHabitActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var deleteDataButton: Button
    private lateinit var deleteGoalsButton: Button
    private lateinit var notificationSwitch: Switch
    private lateinit var themeSwitch: Switch

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
        deleteDataButton = findViewById(R.id.deleteDataButton)
        deleteGoalsButton = findViewById(R.id.deleteGoalsButton)
        notificationSwitch = findViewById(R.id.notificationSwitch)
        themeSwitch = findViewById(R.id.themeSwitch)

        // Set click listener to delete all habits and associated goals
        deleteDataButton.setOnClickListener {
            deleteAllHabits()
        }

        // Set click listener to delete all goals
        deleteGoalsButton.setOnClickListener {
            deleteAllGoals()
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
}
