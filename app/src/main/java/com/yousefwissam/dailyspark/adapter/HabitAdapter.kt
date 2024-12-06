package com.yousefwissam.dailyspark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.model.Habit
    // Adapter for displaying a list of habits in a RecyclerView
class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onHabitClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {
// ViewHolder class for holding references to each view in the habit item layout
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habitName)// Reference to the habit name TextView
        val habitFrequency: TextView = itemView.findViewById(R.id.habitFrequency)// Reference to the habit frequency TextView
    }
// Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }
// Called to bind data to the ViewHolder
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
// Set the habit name and frequency in the corresponding TextViews
        holder.habitName.text = habit.name
        holder.habitFrequency.text = habit.frequency
// Set on click listener to navigate to HabitDetailsActivity when a habit is clicked

        holder.itemView.setOnClickListener {
            onHabitClick(habit)
        }
    }
// Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int = habits.size
// Updates the data set of the adapter and notifies the RecyclerView to refresh the UI
    fun updateData(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}