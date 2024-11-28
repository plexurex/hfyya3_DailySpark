package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val notificationSwitch: Switch = findViewById(R.id.notificationSwitch)
        val themeSwitch: Switch = findViewById(R.id.themeSwitch)
        val deleteDataButton: Button = findViewById(R.id.deleteDataButton)

        // Notification switch logic
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableNotifications()
            } else {
                disableNotifications()
            }
        }

        // Theme switch logic
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Delete all habits logic
        deleteDataButton.setOnClickListener {
            deleteAllHabits()
        }
    }

    private fun enableNotifications() {
        // Logic to enable notifications
        Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
    }

    private fun disableNotifications() {
        // Logic to disable notifications
        Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
    }

    private fun deleteAllHabits() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val collectionRef = db.collection("habits")
                val snapshot = collectionRef.get().await()
                for (document in snapshot.documents) {
                    document.reference.delete().await()
                }
                runOnUiThread {
                    Toast.makeText(this@SettingsActivity, "All data deleted!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@SettingsActivity, "Error deleting data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
