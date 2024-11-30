package com.yousefwissam.dailyspark.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yousefwissam.dailyspark.utils.NotificationUtils

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show the notification
        NotificationUtils.showNotification(
            context,
            "DailySpark Reminder",
            "Don't forget to complete your habit today!"
        )
    }
}
