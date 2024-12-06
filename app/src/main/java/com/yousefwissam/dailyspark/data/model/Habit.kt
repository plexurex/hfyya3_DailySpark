package com.yousefwissam.dailyspark.data.model
// Habit data class representing a habit with various properties
import java.io.Serializable
// Habit data class representing a habit with various properties
data class Habit(
    var id: String = "",// Unique identifier for the habit
    var name: String = "",// Name of the habit
    var frequency: String = "",// Frequency of the habit (e.g., daily, weekly)
    var createdDate: Long = 0L,// Timestamp when the habit was created
    var completed: Boolean = false,// Flag indicating if the habit is completed
    var comment: String = "",// Comment associated with the habit
    var currentStreak: Int = 0,// Current streak of completing the habit
    var longestStreak: Int = 0,// Longest streak of completing the habit
    var lastCompleted: Long = 0,// Timestamp of the last time the habit was completed
    var lastCompletionTime: Long = 0L,// Timestamp of the last completion of the habit
    var userId: String = ""// Identifier of the user associated with the habit
) : Serializable
