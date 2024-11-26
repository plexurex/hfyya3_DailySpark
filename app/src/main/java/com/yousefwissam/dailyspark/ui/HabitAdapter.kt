package com.yousefwissam.dailyspark.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.EditHabitActivity
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val viewModel: HabitViewModel,
    private val onHabitClick: (Habit) -> Unit // New parameter
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habitName)
        val habitFrequency: TextView = itemView.findViewById(R.id.habitFrequency)
        val habitStreak: TextView = itemView.findViewById(R.id.habitStreak)
        val habitCheckbox: CheckBox = itemView.findViewById(R.id.habitCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitName.text = habit.name
        holder.habitFrequency.text = habit.frequency
        holder.habitStreak.text = "Streak: ${habit.streak}"

        holder.itemView.setOnClickListener {
            onHabitClick(habit) // Trigger the click callback
        }

        // Handle checkbox logic
        holder.habitCheckbox.setOnCheckedChangeListener(null) // Clear previous listener
        holder.habitCheckbox.isChecked = false // Default state

        holder.habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                habit.streak += 1
            } else {
                habit.streak = 0
            }
            viewModel.updateHabit(habit)
            holder.habitStreak.text = "Streak: ${habit.streak}"
        }
    }

    override fun getItemCount(): Int = habits.size

    fun updateData(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}

