package com.yousefwissam.dailyspark

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentReference
//import com.google.firebase.firestore.TaskCompletionSource
import com.yousefwissam.dailyspark.data.model.Habit
import com.yousefwissam.dailyspark.data.repository.HabitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class HabitRepositoryTest {

    @Mock
    private lateinit var mockFirestore: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockSnapshot: QuerySnapshot

    @Mock
    private lateinit var mockDocument: DocumentReference

    private lateinit var habitRepository: HabitRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock Firebase collection reference
        `when`(mockFirestore.collection(anyString())).thenReturn(mockCollection)

        habitRepository = HabitRepository(mockFirestore)
    }

    @Test
    fun `test getAllHabits returns correct habits list`() = runBlocking {
        val habitList = listOf(
            Habit(id = "1", name = "Read Book", frequency = "Daily"),
            Habit(id = "2", name = "Workout", frequency = "Weekly")
        )

        // Mock snapshot result
        whenever(mockSnapshot.toObjects(Habit::class.java)).thenReturn(habitList)

        // Create a TaskCompletionSource and set the result to mockSnapshot
        val taskCompletionSource = TaskCompletionSource<QuerySnapshot>()
        taskCompletionSource.setResult(mockSnapshot)
        val mockTask: Task<QuerySnapshot> = taskCompletionSource.task

        // Mock collection get() call to return the Task
        whenever(mockCollection.get()).thenReturn(mockTask)

        val result = habitRepository.getAllHabits()

        // Verify the result
        assert(result == habitList)
    }

    @Test
    fun `test markHabitAsCompleted updates habit correctly`(): Unit = runBlocking {
        val habit = Habit(id = "1", name = "Read Book", frequency = "Daily", currentStreak = 0)

        // Mock document reference to return the right value when updated
        whenever(mockCollection.document(habit.id)).thenReturn(mockDocument)

        // Mock the set() method to complete successfully without exception
        val setTaskCompletionSource = TaskCompletionSource<Void>()
        setTaskCompletionSource.setResult(null)
        whenever(mockDocument.set(any(Habit::class.java))).thenReturn(setTaskCompletionSource.task)

        // Mark the habit as completed
        habitRepository.markHabitAsCompleted(habit)

        // Verify that document reference set() was called
        verify(mockDocument).set(any(Habit::class.java))
    }
}
