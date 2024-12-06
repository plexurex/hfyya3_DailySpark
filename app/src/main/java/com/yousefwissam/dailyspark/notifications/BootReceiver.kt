package com.yousefwissam.dailyspark.notifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity
// BootReceiver class to handle device reboots and reschedule notifications
class BootReceiver : BroadcastReceiver() {// Handle device reboots
    override fun onReceive(context: Context, intent: Intent) {  // Reschedule notifications on boot
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted, rescheduling notifications.")// Schedule daily notifications
            SettingsActivity.scheduleDailyNotification(context)
        }
    }
}
