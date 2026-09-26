package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ConversationEntity
import com.example.data.EvolutionStateEntity
import com.example.engine.EvolutionEngine
import com.example.engine.OfflineAiBrain
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("EvoAI", appName)
    }

    @Test
    fun `test offline brain generates intelligent response`() = runBlocking {
        val brain = OfflineAiBrain()
        val state = EvolutionStateEntity()
        val response = brain.generateResponse(
            prompt = "Philosophical Sage",
            userSpeech = "What is consciousness?",
            history = emptyList(),
            evolutionState = state
        )
        assertNotNull(response)
        assertTrue(response.isNotBlank())
    }

    @Test
    fun `test evolution engine advances generation and traits`() {
        val initialState = EvolutionStateEntity(generation = 1)
        val dummyConvs = listOf(
            ConversationEntity(
                id = 1,
                prompt = "Philosopher",
                personaName = "Philosopher",
                startTime = 1000L,
                endTime = 2000L,
                messageCount = 4,
                dialogJson = "[]",
                learnedConcepts = "consciousness, truth, knowledge",
                summary = "Discussion on consciousness"
            )
        )

        val result = EvolutionEngine.executeEvolution(initialState, dummyConvs)
        assertEquals(2, result.updatedState.generation)
        assertTrue(result.updatedState.synapseCount > initialState.synapseCount)
        assertNotNull(result.newTraitUnlocked)
    }
}
