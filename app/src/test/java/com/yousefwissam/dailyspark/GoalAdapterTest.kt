package com.yousefwissam.dailyspark

import com.yousefwissam.dailyspark.data.model.Goal
import org.junit.Assert.assertEquals
import org.junit.Test
import com.yousefwissam.dailyspark.adapter.GoalAdapter

class GoalAdapterTest {

    @Test
    fun `test item count`() {
        val goals = listOf(
            Goal(id = "1", description = "Goal 1", targetDays = 10, progress = 5),
            Goal(id = "2", description = "Goal 2", targetDays = 20, progress = 15)
        )
        val adapter = GoalAdapter(goals)
        assertEquals(2, adapter.itemCount)
    }
}
