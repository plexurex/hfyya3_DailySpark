package com.yousefwissam.dailyspark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.model.Goal

// Adapter for displaying a list of goals in a RecyclerView
class GoalAdapter(private val goals: List<Goal>) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {
    // ViewHolder class for holding references to each view in the goal item layout
    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TextView for displaying the goal description
        val goalDescriptionTextView: TextView = view.findViewById(R.id.goalDescriptionTextView)
        // TextView for displaying the goal progress
        val goalProgressTextView: TextView = view.findViewById(R.id.goalProgressTextView)
    }
    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        // Inflate the goal item layout and create a new ViewHolder
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        // Return the new ViewHolder
        return GoalViewHolder(view)
    }
    // Called to bind data to the ViewHolder
    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        // Get the goal at the current position
        val goal = goals[position]
        // Set the goal description and progress in the corresponding TextViews
        holder.goalDescriptionTextView.text = goal.description
        // Format the progress text and set it in the TextView
        holder.goalProgressTextView.text = "Progress: ${goal.progress}/${goal.targetDays}"
    }
    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount() = goals.size
}
