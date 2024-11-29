package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.yousefwissam.dailyspark.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Find views using findViewById
        val logoImageView: ImageView = findViewById(R.id.logoImageView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loadingText: TextView = findViewById(R.id.loadingText)

        // Wait for 3 seconds before transitioning to the MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // 3 seconds delay
    }
}