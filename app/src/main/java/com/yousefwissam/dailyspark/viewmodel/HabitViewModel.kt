package com.yousefwissam.dailyspark.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefwissam.dailyspark.data.model.Habit
import com.yousefwissam.dailyspark.data.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository,
    private val context: Context
) : ViewModel() {

    private val _habits = MutableLiveData<List<Habit>>()
    val habits: LiveData<List<Habit>> get() = _habits

    init {
        loadHabits()
    }
    // Function to load habits from the repository
    fun loadHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val habitList = repository.getAllHabits()
                _habits.postValue(habitList)
            } catch (e: Exception) {
                _habits.postValue(emptyList())
            }
        }
    }
}
