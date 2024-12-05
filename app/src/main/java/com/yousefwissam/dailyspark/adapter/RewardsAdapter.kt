package com.yousefwissam.dailyspark.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.model.Reward

class RewardsAdapter(private val rewards: List<Reward>) : RecyclerView.Adapter<RewardsAdapter.RewardViewHolder>() {

    class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rewardName: TextView = itemView.findViewById(R.id.textViewRewardName)
        val rewardPoints: TextView = itemView.findViewById(R.id.textViewRewardPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.rewardName.text = reward.badgeName
        holder.rewardPoints.text = "Points: ${reward.points}"
    }

    override fun getItemCount() = rewards.size
}
