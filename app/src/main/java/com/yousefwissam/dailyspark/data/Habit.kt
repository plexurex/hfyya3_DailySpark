package com.yousefwissam.dailyspark.data

import java.io.Serializable

data class Habit(
    var id: String = "",
    val name: String = "",
    val frequency: String = "",
    val createdDate: Long = 0L,
    var completed: Boolean = false,        // New field to track if habit is completed
    var comment: String = ""               // New field to store any comment
) : Serializable
