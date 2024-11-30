package com.yousefwissam.dailyspark.data

import java.io.Serializable

data class Habit(
    var id: String = "", // Ensure there's a default value
    var name: String = "", // Default value for the name
    var frequency: String = "", // Default value for frequency
    var createdDate: Long = 0L, // Default value for createdDate
    var completed: Boolean = false, // Default value for completed
    var comment: String = "", // Default value for comment
    var currentStreak: Int = 0,  // Tracks the current consecutive days completed
    var longestStreak: Int = 0,   // The longest streak of days the habit has been completed consecutively
    var lastCompleted: Long = 0 // Timestamp of the last completion

) : Serializable
