package com.yousefwissam.dailyspark

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.repository.HabitRepository
import com.yousefwissam.dailyspark.viewmodel.HabitViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HabitViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var habitRepository: HabitRepository

    @Mock
    private lateinit var habitObserver: Observer<List<Habit>>

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: HabitViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = HabitViewModel(habitRepository, context)
    }

    @Test
    fun `loadHabits updates habits LiveData correctly`() = runBlocking {
        val habitList = listOf(Habit(id = "1", name = "Read Book", completed = false))
        `when`(habitRepository.getAllHabits()).thenReturn(habitList)

        viewModel.habits.observeForever(habitObserver)

        viewModel.loadHabits()

        Mockito.verify(habitObserver).onChanged(habitList)
    }
}
