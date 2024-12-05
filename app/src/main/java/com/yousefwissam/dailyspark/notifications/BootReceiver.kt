package com.yousefwissam.dailyspark.notifications


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yousefwissam.dailyspark.ui.settings.SettingsActivity

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted, rescheduling notifications.")
            SettingsActivity.scheduleDailyNotification(context)
        }
    }
}
