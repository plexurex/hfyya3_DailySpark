package com.yousefwissam.dailyspark

import com.yousefwissam.dailyspark.data.Goal
import com.yousefwissam.dailyspark.data.Habit
import com.yousefwissam.dailyspark.data.Reward
import org.junit.Assert.assertEquals
import org.junit.Test

class DataClassTests {

    @Test
    fun `test Goal default values`() {
        val goal = Goal()
        assertEquals("", goal.id)
        assertEquals("", goal.habitId)
        assertEquals("", goal.habitName)
        assertEquals("", goal.description)
        assertEquals(0, goal.targetDays)
        assertEquals(0, goal.progress)
    }

    @Test
    fun `test Habit default values`() {
        val habit = Habit()
        assertEquals("", habit.id)
        assertEquals("", habit.name)
        assertEquals("", habit.frequency)
        assertEquals(0L, habit.createdDate)
        assertEquals(false, habit.completed)
        assertEquals("", habit.comment)
        assertEquals(0, habit.currentStreak)
        assertEquals(0, habit.longestStreak)
        assertEquals(0L, habit.lastCompleted)
        assertEquals(0L, habit.lastCompletionTime)
    }

    @Test
    fun `test Reward default values`() {
        val reward = Reward()
        assertEquals("", reward.badgeName)
        assertEquals(0, reward.points)
    }
}
