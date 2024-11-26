package com.yousefwissam.dailyspark.viewmodel

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.yousefwissam.dailyspark.R
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {
    val allHabits: LiveData<List<Habit>> = repository.allHabits.asLiveData()

    fun insertHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(habit)
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHabitById(id)
        }
    }

    fun getHabitById(id: Int): LiveData<Habit> {
        return repository.getHabitById(id).asLiveData()
    }

    fun deleteHabitById(habitId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHabitById(habitId) // Uses repository method
        }
    }
}

class HabitViewModelFactory(private val repository: HabitRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            return HabitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val habitName: TextView = itemView.findViewById(R.id.habitName)
    val habitFrequency: TextView = itemView.findViewById(R.id.habitFrequency)
    val habitStreak: TextView = itemView.findViewById(R.id.habitStreak)
    val habitCheckbox: CheckBox = itemView.findViewById(R.id.habitCheckbox)
}