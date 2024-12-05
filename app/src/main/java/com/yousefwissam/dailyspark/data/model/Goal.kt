package com.yousefwissam.dailyspark.data.model

data class Goal(
    var id: String = "",
    var habitId: String = "",
    var habitName: String = "",
    var description: String = "",
    var targetDays: Int = 0,
    var progress: Int = 0
)
