package com.yousefwissam.dailyspark.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val frequency: String,
    val createdDate: Long,  // Timestamp of when the habit was created
    var streak: Int = 0
)
