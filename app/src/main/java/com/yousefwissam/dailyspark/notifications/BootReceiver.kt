package com.yousefwissam.dailyspark.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import java.util.*
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val preferences = context.getSharedPreferences(
                "com.yousefwissam.dailyspark.PREFERENCES", Context.MODE_PRIVATE
            )
            val isNotificationEnabled = preferences.getBoolean("NOTIFICATION_ENABLED", false)
            if (isNotificationEnabled) {
                // Schedule notification with AlarmManager
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val alarmIntent = Intent(context, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 9)  // Set notification time to 9 AM
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

                Log.d("BootReceiver", "AlarmManager Notification rescheduled for: ${calendar.time}")
            }
        }
    }
}
