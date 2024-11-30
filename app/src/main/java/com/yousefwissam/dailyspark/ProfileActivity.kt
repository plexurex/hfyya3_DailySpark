package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.yousefwissam.dailyspark.adapter.GoalAdapter
import com.yousefwissam.dailyspark.data.Goal
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.ui.EditHabitActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var badgeImageView: ImageView  // Correctly reference the ImageView for badges
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    // Goal-related UI components
    private lateinit var goalDescriptionEditText: EditText
    private lateinit var goalTargetEditText: EditText
    private lateinit var habitSelectionSpinner: Spinner
    private lateinit var addGoalButton: Button
    private lateinit var goalsRecyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val userId = "currentUser" // Replace with actual user ID

    private val goals = mutableListOf<Goal>()
    private val habits = mutableListOf<Habit>()
    private var selectedHabit: Habit? = null
    private lateinit var goalAdapter: GoalAdapter

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

        // Initialize goal-related UI components
        goalDescriptionEditText = findViewById(R.id.goalDescriptionEditText)
        goalTargetEditText = findViewById(R.id.goalTargetEditText)
        habitSelectionSpinner = findViewById(R.id.habitSelectionSpinner)
        addGoalButton = findViewById(R.id.addGoalButton)
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView)

        setupRecyclerView()
        loadUserProfile()
        setupNavigation()
        loadUserGoals()
        loadUserHabits()

        addGoalButton.setOnClickListener {
            addGoal()
        }

        habitSelectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedHabit = habits[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedHabit = null
            }
        }
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
        val user = auth.currentUser
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        usernameTextView.text = name ?: "User"
                    } else {
                        usernameTextView.text = "User"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun displayBadges(currentPoints: Int) {
        val pointsThresholds = arrayOf(50, 100, 150, 200, 300)
        val badges = arrayOf(
            R.drawable.badge_5_days,
            R.drawable.badge_10_days,
            R.drawable.badge_15_days,
            R.drawable.badge_20_days,
            R.drawable.badge_25_days
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

    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter(goals)
        goalsRecyclerView.layoutManager = LinearLayoutManager(this)
        goalsRecyclerView.adapter = goalAdapter
    }

    private fun loadUserGoals() {
        db.collection("goals").document(userId).collection("userGoals").get()
            .addOnSuccessListener { documents ->
                goals.clear()
                for (document in documents) {
                    val goal = document.toObject<Goal>()
                    goal.id = document.id
                    goals.add(goal)
                }
                goalAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading goals", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadUserHabits() {
        db.collection("habits").get()
            .addOnSuccessListener { result ->
                habits.clear()
                for (document in result) {
                    val habit = document.toObject(Habit::class.java)
                    habit.id = document.id
                    habits.add(habit)
                }

                val habitNames = habits.map { it.name }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, habitNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                habitSelectionSpinner.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading habits", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addGoal() {
        val description = goalDescriptionEditText.text.toString()
        val targetDays = goalTargetEditText.text.toString().toIntOrNull()

        if (description.isNotBlank() && targetDays != null && targetDays > 0 && selectedHabit != null) {
            val newGoal = Goal(
                habitId = selectedHabit!!.id,
                habitName = selectedHabit!!.name,
                description = description,
                targetDays = targetDays,
                progress = 0
            )

            db.collection("goals").document(userId).collection("userGoals").add(newGoal)
                .addOnSuccessListener { documentReference ->
                    newGoal.id = documentReference.id
                    goals.add(newGoal)
                    goalAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Goal added successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error adding goal", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select a habit and enter valid goal details.", Toast.LENGTH_SHORT).show()
        }
    }
}
