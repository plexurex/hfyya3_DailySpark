package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.data.HabitDatabase
import com.yousefwissam.dailyspark.ui.HabitAdapter
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel
import com.yousefwissam.dailyspark.viewmodel.HabitViewModelFactory
import com.yousefwissam.dailyspark.repository.HabitRepository

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var addHabitButton: Button

    private val viewModel: HabitViewModel by viewModels {
        val database = HabitDatabase.getDatabase(application)
        val repository = HabitRepository(database.habitDao())
        HabitViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(
            mutableListOf(),
            viewModel
        ) { habit -> // Handle habit clicks
            val intent = Intent(this, EditHabitActivity::class.java)
            intent.putExtra("HABIT_ID", habit.id) // Pass the habit ID
            startActivity(intent)
        }
        recyclerView.adapter = habitAdapter

        addHabitButton = findViewById(R.id.addHabitButton)
        val editHabitButton: Button = findViewById(R.id.editHabitButton)

        addHabitButton.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            startActivity(intent)
        }

        editHabitButton.setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java)
            startActivity(intent)
        }

        viewModel.allHabits.observe(this) { habits ->
            habitAdapter.updateData(habits)
        }
    }
}
