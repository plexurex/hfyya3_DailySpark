package com.yousefwissam.dailyspark.data



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    val name: String,
    val frequency: String,
    val createdDate: Long
)
