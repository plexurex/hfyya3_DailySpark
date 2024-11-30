package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.EditHabitActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var badgeImageView: ImageView  // Correctly reference the ImageView for badges
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize UI components
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        profileImageView = findViewById(R.id.profileImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        pointsTextView = findViewById(R.id.pointsTextView)
        badgeImageView = findViewById(R.id.badgeImageView)  // Correct reference to the ImageView

        loadUserProfile()
        setupNavigation()
    }

    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_main_menu -> startActivity(Intent(this, MainActivity::class.java))
                R.id.nav_add_habit -> startActivity(Intent(this, AddHabitActivity::class.java))
                R.id.nav_edit_habit -> startActivity(Intent(this, EditHabitActivity::class.java))
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_profile -> drawerLayout.closeDrawers()
            }
            true
        }
    }

    private fun loadUserProfile() {
        profileImageView.setImageResource(R.drawable.profile_pic)  // Use your drawable resource

        val userId = "currentUser"
        db.collection("rewards").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentPoints = document.getLong("points") ?: 0
                    usernameTextView.text = "Yousef Wisam"
                    pointsTextView.text = "Points: $currentPoints"
                    displayBadges(currentPoints.toInt())
                } else {
                    usernameTextView.text = "Yousef Wisam"
                    pointsTextView.text = "Points: 0"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading profile data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayBadges(currentPoints: Int) {
        val pointsThresholds = arrayOf(50, 100, 150, 200, 300)
        val badges = arrayOf(
            R.drawable.badge_5_days,
            R.drawable.badge_10_days,
            R.drawable.badge_15_days,
            R.drawable.badge_20_days,
            R.drawable.badge_30_days
        )

        var badgeToShow: Int? = null
        for (i in pointsThresholds.indices) {
            if (currentPoints >= pointsThresholds[i]) {
                badgeToShow = badges[i]
            } else {
                break
            }
        }

        badgeImageView.apply {
            if (badgeToShow != null) {
                setImageResource(badgeToShow)
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
    }
}
