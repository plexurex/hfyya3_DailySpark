package com.yousefwissam.dailyspark.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R  // Make sure this is here!
import com.yousefwissam.dailyspark.data.Habit



class HabitAdapter(private val habitList: List<Habit>) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitList[position]
        holder.bind(habit)
    }

    override fun getItemCount(): Int {
        return habitList.size
    }

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(habit: Habit) {
            itemView.findViewById<TextView>(R.id.habitName).text = habit.name
            itemView.findViewById<TextView>(R.id.habitFrequency).text = habit.frequency.toString()
        }
    }
}