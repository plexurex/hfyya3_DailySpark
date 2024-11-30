package com.yousefwissam.dailyspark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.Goal

class GoalAdapter(private val goals: List<Goal>) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goalDescriptionTextView: TextView = view.findViewById(R.id.goalDescriptionTextView)
        val goalProgressTextView: TextView = view.findViewById(R.id.goalProgressTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.goalDescriptionTextView.text = goal.description
        holder.goalProgressTextView.text = "Progress: ${goal.progress}/${goal.targetDays}"
    }

    override fun getItemCount() = goals.size
}
