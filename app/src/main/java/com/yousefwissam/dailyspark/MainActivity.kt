package com.yousefwissam.dailyspark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.yousefwissam.dailyspark.ui.HabitAdapter
import com.yousefwissam.dailyspark.data.Habit
import androidx.appcompat.widget.Toolbar
import com.yousefwissam.dailyspark.ui.EditHabitActivity

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var addHabitButton: Button
    private lateinit var editHabitButton: Button

    // Initialize Firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up Toolbar as ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAdapter(mutableListOf()) { habit ->
            navigateToHabitDetails(habit)
        }
        recyclerView.adapter = habitAdapter

        addHabitButton = findViewById(R.id.addHabitButton)
        editHabitButton = findViewById(R.id.editHabitButton)

        addHabitButton.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            startActivity(intent)
        }

        editHabitButton.setOnClickListener {
            val intent = Intent(this, EditHabitActivity::class.java)
            startActivity(intent)
        }

        // Real-time listener for changes in habits collection
        db.collection("habits")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val habits = snapshot.documents.mapNotNull { document ->
                        document.toObject(Habit::class.java)?.copy(id = document.id)
                    }
                    habitAdapter.updateData(habits)
                }
            }
    }

    private fun navigateToHabitDetails(habit: Habit) {
        val intent = Intent(this, HabitDetailsActivity::class.java)
        intent.putExtra("HABIT_ID", habit.id)
        startActivity(intent)
    }
}
