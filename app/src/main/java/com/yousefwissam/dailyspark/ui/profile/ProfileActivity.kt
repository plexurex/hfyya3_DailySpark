package com.yousefwissam.dailyspark.ui.profile

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
import com.yousefwissam.dailyspark.ui.main.MainActivity
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity
import com.yousefwissam.dailyspark.adapter.GoalAdapter
import com.yousefwissam.dailyspark.data.model.Goal
import com.yousefwissam.dailyspark.data.model.Habit
import com.yousefwissam.dailyspark.ui.habit.EditHabitActivity
import com.yousefwissam.dailyspark.ui.habit.AddHabitActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var pointsTextView: TextView
    private lateinit var badgeImageView: ImageView
    private lateinit var editNameIcon: ImageView
    private lateinit var usernameEditText: EditText
    private lateinit var saveNameButton: Button
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // Goal-related UI components
    private lateinit var goalDescriptionEditText: EditText
    private lateinit var goalTargetEditText: EditText
    private lateinit var habitSelectionSpinner: Spinner
    private lateinit var addGoalButton: Button
    private lateinit var goalsRecyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private val userId = auth.currentUser?.uid ?: "currentUser" // Get the actual user ID from Firebase Auth

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
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        profileImageView = findViewById(R.id.profileImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        pointsTextView = findViewById(R.id.pointsTextView)
        badgeImageView = findViewById(R.id.badgeImageView)
        editNameIcon = findViewById(R.id.editNameIcon)
        usernameEditText = findViewById(R.id.usernameEditText)
        saveNameButton = findViewById(R.id.saveNameButton)

        // Set up edit functionality for username
        editNameIcon.setOnClickListener {
            // Show EditText and Save button
            usernameTextView.visibility = View.GONE
            usernameEditText.visibility = View.VISIBLE
            saveNameButton.visibility = View.VISIBLE
            usernameEditText.setText(usernameTextView.text)
        }

        saveNameButton.setOnClickListener {
            val newName = usernameEditText.text.toString().trim()
            if (newName.isNotEmpty()) {
                updateUserName(newName)
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize goal-related UI components
        goalDescriptionEditText = findViewById(R.id.goalDescriptionEditText)
        goalTargetEditText = findViewById(R.id.goalTargetEditText)
        habitSelectionSpinner = findViewById(R.id.habitSelectionSpinner)
        addGoalButton = findViewById(R.id.addGoalButton)
        goalsRecyclerView = findViewById(R.id.goalsRecyclerView)

        setupRecyclerView()
        loadUserProfile()
        loadUserPoints()
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
    // Handle navigation item selection
    private fun setupNavigation() {
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
            }
            true
        }
    }
    // Load user profile and update UI
    private fun loadUserProfile() {
        val user = auth.currentUser
        user?.let { currentUser ->
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        usernameTextView.text = name ?: "User"
                        usernameEditText.setText(name ?: "")
                    } else {
                        usernameTextView.text = "User"
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
    // Update user name
    private fun updateUserName(newName: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .update("name", newName)
                .addOnSuccessListener {
                    Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()
                    usernameTextView.text = newName
                    usernameTextView.visibility = View.VISIBLE
                    usernameEditText.visibility = View.GONE
                    saveNameButton.visibility = View.GONE
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show()
                }
        }
    }
    // Load user points
    private fun loadUserPoints() {
        db.collection("rewards").whereEqualTo("userid", userId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents.first()
                    val currentPoints = document.getLong("points") ?: 0
                    pointsTextView.text = "Points: $currentPoints"
                    displayBadges(currentPoints.toInt())  // Update badge based on points
                } else {
                    pointsTextView.text = "Points: 0"
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading points", Toast.LENGTH_SHORT).show()
            }
    }
    // Display badges based on points
    private fun displayBadges(currentPoints: Int) {
        val pointsThresholds = arrayOf(50, 100, 150, 200, 300)
        val badges = arrayOf(
            R.drawable.badge_5_days,// 50 points
            R.drawable.badge_10_days,// 100 points
            R.drawable.badge_15_days,// 150 points
            R.drawable.badge_20_days,// 200 points
            R.drawable.badge_25_days // 250 points
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
    // Set up RecyclerView
    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter(goals)
        goalsRecyclerView.layoutManager = LinearLayoutManager(this)
        goalsRecyclerView.adapter = goalAdapter
    }
    // Load user goals
    private fun loadUserGoals() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            db.collection("goals").document(it.uid).collection("userGoals")
                .get()
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
    }

    // Load user habits
    private fun loadUserHabits() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            db.collection("habits")
                .whereEqualTo("userId", it.uid) // Filter by user ID
                .get()
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
    }
    // Add a new goal
    private fun addGoal() {
        val description = goalDescriptionEditText.text.toString()
        val targetDays = goalTargetEditText.text.toString().toIntOrNull()

        if (description.isNotBlank() && targetDays != null && targetDays > 0 && selectedHabit != null) {
            val newGoal = Goal(
                habitId = selectedHabit!!.id,// Set the habit ID
                habitName = selectedHabit!!.name,// Set the habit name
                description = description,// Set the goal description
                targetDays = targetDays,// Set the target days
                progress = 0// Initialize progress to 0
            )
            // Add the goal to Firestore
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
