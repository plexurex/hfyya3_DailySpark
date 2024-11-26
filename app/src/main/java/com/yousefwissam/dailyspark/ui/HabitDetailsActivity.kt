package com.yousefwissam.dailyspark.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yousefwissam.dailyspark.EditHabitActivity
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel
import com.yousefwissam.dailyspark.viewmodel.HabitViewModelFactory
import com.yousefwissam.dailyspark.repository.HabitRepository
import com.yousefwissam.dailyspark.data.HabitDatabase

class HabitDetailsActivity : AppCompatActivity() {

    private lateinit var habitNameTextView: TextView
    private lateinit var habitFrequencyTextView: TextView
    private lateinit var editButton: Button

    private val viewModel: HabitViewModel by viewModels {
        HabitViewModelFactory(HabitRepository(HabitDatabase.getDatabase(this).habitDao()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_details)

        habitNameTextView = findViewById(R.id.textViewHabitName)
        habitFrequencyTextView = findViewById(R.id.textViewHabitFrequency)
        editButton = findViewById(R.id.buttonEdit)

        val habitId = intent.getIntExtra("HABIT_ID", -1)

        // Load the habit data
        if (habitId != -1) {
            viewModel.getHabitById(habitId).observe(this) { habit ->
                habitNameTextView.text = habit.name
                habitFrequencyTextView.text = habit.frequency

                // Setup edit button click listener
                editButton.setOnClickListener {
                    val intent = Intent(this, EditHabitActivity::class.java)
                    intent.putExtra("HABIT_ID", habit.id)
                    startActivity(intent)
                }
            }
        }
    }
}
