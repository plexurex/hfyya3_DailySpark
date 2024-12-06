package com.yousefwissam.dailyspark

import com.yousefwissam.dailyspark.data.model.Goal
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yousefwissam.dailyspark.adapter.GoalAdapter
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.`when`

class GoalAdapterTest {
    // Test cases for GoalAdapter
    private fun mockLayoutInflater(): LayoutInflater {
        val li = mock(LayoutInflater::class.java)
        val viewGroup = mock(ViewGroup::class.java)
        `when`(li.inflate(anyInt(), eq(viewGroup), anyBoolean())).thenAnswer {
            mock(View::class.java)
        }
        return li
    }

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
