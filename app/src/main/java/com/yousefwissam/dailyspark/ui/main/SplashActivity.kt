package com.yousefwissam.dailyspark.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.ui.AuthenticationActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()
        // Delay for 3 seconds before checking user authentication
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is already logged in
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not logged in
                startActivity(Intent(this, AuthenticationActivity::class.java))
            }
            finish()
        }, 3000) // Show splash screen for 3 seconds
    }
}
