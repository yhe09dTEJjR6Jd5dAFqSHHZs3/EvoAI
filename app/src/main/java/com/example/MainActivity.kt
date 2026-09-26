package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.ConversationDetailDialog
import com.example.ui.EvolutionScreen
import com.example.ui.HomeScreen
import com.example.ui.MainViewModel
import com.example.ui.VoiceConversationScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EvoAiApp()
                }
            }
        }
    }
}

@Composable
fun EvoAiApp(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedConv by viewModel.selectedConversation.collectAsStateWithLifecycle()
    val isChinese by viewModel.isChinese.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            AppScreen.HOME -> HomeScreen(viewModel = viewModel)
            AppScreen.VOICE_CONVERSATION -> VoiceConversationScreen(viewModel = viewModel)
            AppScreen.EVOLUTION_CEREMONY -> EvolutionScreen(viewModel = viewModel)
        }
    }

    selectedConv?.let { conv ->
        val messages = viewModel.allConversations.value.find { it.id == conv.id }?.let {
            viewModel.allConversations // repository has parser
        }
        val parsedMessages = rememberParsedMessages(viewModel, conv.dialogJson)

        ConversationDetailDialog(
            conversation = conv,
            messages = parsedMessages,
            isChinese = isChinese,
            onDismiss = { viewModel.dismissConversationDetail() },
            onDelete = { viewModel.deleteConversation(conv.id) }
        )
    }
}

@Composable
private fun rememberParsedMessages(
    viewModel: MainViewModel,
    dialogJson: String
): List<com.example.data.DialogMessage> {
    return androidx.compose.runtime.remember(dialogJson) {
        val list = mutableListOf<com.example.data.DialogMessage>()
        try {
            val array = org.json.JSONArray(dialogJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    com.example.data.DialogMessage(
                        sender = obj.optString("sender", "unknown"),
                        text = obj.optString("text", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        list
    }
}
