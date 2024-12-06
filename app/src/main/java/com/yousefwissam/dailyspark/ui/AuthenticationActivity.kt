package com.yousefwissam.dailyspark.ui

import android.content.Context  // Corrected import
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.main.MainActivity
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity.Companion.scheduleDailyNotification

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private val db = FirebaseFirestore.getInstance()
    // Initialize Firebase Auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in, if so redirect to MainActivity
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_authentication)

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        registerButton = findViewById(R.id.buttonRegister)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Handle login button click
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Handle registration button click
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Save user details to Firestore
                        val userData = mapOf("email" to user.email, "name" to "User")
                        db.collection("users").document(user.uid).set(userData)
                    }
                    updateUI(user)
                } else {
                    Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    // Update UI based on authentication result
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Ask for notification permission after successful registration or login
            val dialog = AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("Would you like to receive daily motivational notifications?")
                .setPositiveButton("Yes") { _, _ ->
                    // Enable notifications
                    val sharedPreferences = getSharedPreferences("com.yousefwissam.dailyspark.PREFERENCES", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("NOTIFICATION_ENABLED", true).apply()
                    scheduleDailyNotification(this)  // Schedule the notification immediately if they accept
                }
                .setNegativeButton("No") { _, _ ->
                    // Do nothing, notifications won't be scheduled
                }
                .create()
            dialog.show()

            // Proceed to the Main Activity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
