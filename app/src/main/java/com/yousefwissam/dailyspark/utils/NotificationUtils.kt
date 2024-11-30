package com.yousefwissam.dailyspark.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.notifications.NotificationReceiver
import java.util.Calendar

object NotificationUtils {

    const val CHANNEL_ID = "DAILY_SPARK_NOTIFICATION_CHANNEL"
    private const val CHANNEL_NAME = "DailySpark Notifications"
    private const val CHANNEL_DESCRIPTION = "Daily reminder notifications for habits"

    // Function to create the notification channel
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("NotificationUtils", "Notification channel created successfully.")
        }
    }

    // Method to show a notification
    fun showNotification(context: Context, title: String, content: String) {
        // Check notification permission for Android 13 (API level 33) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("NotificationUtils", "Notification permission not granted.")
                return // Exit if the permission is not granted
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }
    }

    // Method to schedule a daily notification using AlarmManager
    fun scheduleDailyNotification(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 9)  // Set notification time to 9 AM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)

                // If the scheduled time has already passed, set it for the next day
                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.d("NotificationUtils", "Notification scheduled for: ${calendar.time}")

        } catch (e: SecurityException) {
            Log.e("NotificationUtils", "SecurityException: ${e.message}")
            Toast.makeText(context, "Please provide the necessary permissions for notifications.", Toast.LENGTH_SHORT).show()
        }
    }


    // Method to cancel the daily notification
    fun cancelDailyNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
