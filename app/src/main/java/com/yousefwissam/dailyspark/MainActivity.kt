package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.ui.HabitAdapter


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitList: List<Habit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addHabitButton = findViewById<Button>(R.id.addHabitButton)
        addHabitButton.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            startActivity(intent)
        }

        // Initialize habit list with sample data
        habitList = listOf(
            Habit("Exercise", "Daily", System.currentTimeMillis()),
            Habit("Read", "Weekly", System.currentTimeMillis()),
            Habit("Meditate", "Daily", System.currentTimeMillis())
        )

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(habitList)
        recyclerView.adapter = habitAdapter
    }
}
