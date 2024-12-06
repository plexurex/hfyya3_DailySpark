package com.yousefwissam.dailyspark.data.model
// Data class representing a goal with various properties
data class Goal(
    var id: String = "",// Unique identifier for the goal
    var habitId: String = "",// Identifier of the habit associated with the goal
    var habitName: String = "",// Name of the habit associated with the goal
    var description: String = "",// Description of the goal
    var targetDays: Int = 0,// Target number of days for completing the goal
    var progress: Int = 0// Current progress of the goal
)
