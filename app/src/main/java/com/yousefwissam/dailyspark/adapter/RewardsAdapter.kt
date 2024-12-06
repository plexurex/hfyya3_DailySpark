package com.yousefwissam.dailyspark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.model.Reward
// Adapter for displaying a list of rewards in a RecyclerView
class RewardsAdapter(private val rewards: List<Reward>) : RecyclerView.Adapter<RewardsAdapter.RewardViewHolder>() {
// ViewHolder class for holding references to each view in the reward item layout
    class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rewardName: TextView = itemView.findViewById(R.id.textViewRewardName)
        val rewardPoints: TextView = itemView.findViewById(R.id.textViewRewardPoints)
    }
// Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }
// Called to bind data to the ViewHolder
    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.rewardName.text = reward.badgeName
        holder.rewardPoints.text = "Points: ${reward.points}"
    }
// Returns the total number of items in the data set held by the adapter
    override fun getItemCount() = rewards.size
}