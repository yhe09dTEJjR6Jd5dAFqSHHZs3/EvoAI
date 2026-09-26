package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DialogMessage
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonTeal

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VoiceConversationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isChinese by viewModel.isChinese.collectAsStateWithLifecycle()
    val promptText by viewModel.promptText.collectAsStateWithLifecycle()
    val messages by viewModel.currentDialogMessages.collectAsStateWithLifecycle()
    val aiVoiceState by viewModel.aiVoiceState.collectAsStateWithLifecycle()
    val isListening by viewModel.voiceManager.isListening.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsStateWithLifecycle()
    val rmsLevel by viewModel.voiceManager.rmsLevel.collectAsStateWithLifecycle()
    val showEndConfirmDialog by viewModel.showEndConfirmDialog.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            viewModel.voiceManager.startListening(isChinese)
        }
    }

    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // BackHandler prompts confirmation dialog
    BackHandler {
        viewModel.onEndButtonClicked()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF090D14),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isChinese) "实时语音对话" else "Live Voice Dialogue",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = promptText.take(28),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEndButtonClicked() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "End and close",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Visual mic status pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isListening) CyberGreen.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.4f),
                        border = if (isListening) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberGreen, NeonTeal))) else null,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isListening) CyberGreen else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isListening) (if (isChinese) "聆听中" else "Listening") else (if (isChinese) "静待中" else "Idle"),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isListening) CyberGreen else Color.LightGray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D121D))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Permission Banner if not granted
            if (!hasMicPermission) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261D10))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isChinese) "请授予麦克风录音权限以便用嘴说话" else "Microphone permission needed to speak",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberAmber,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isChinese) "授权" else "Grant",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Glowing AI Visualizer Core
            AiOrbVisualizer(
                isListening = isListening,
                isSpeaking = isSpeaking,
                aiVoiceState = aiVoiceState,
                rmsLevel = rmsLevel,
                onOrbClicked = { viewModel.triggerMicToggle() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Voice Status Badge
            VoiceStatusBadge(
                aiVoiceState = aiVoiceState,
                isListening = isListening,
                isSpeaking = isSpeaking,
                isChinese = isChinese
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Live Dialog Messages Scroll
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111724)),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(CyberCyan.copy(alpha = 0.25f), Color.Transparent)))
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        MessageBubble(msg = msg, isChinese = isChinese)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spoken prompt helper suggestions (enables instant vocal conversation even in silent emulators!)
            SpokenSuggestionsBar(
                isChinese = isChinese,
                onSuggestionSelected = { spoken ->
                    viewModel.onUserVoiceCaptured(spoken)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Bottom Action: "结束" (End) Button with distinct high-contrast styling
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic toggle button
                IconButton(
                    onClick = {
                        if (!hasMicPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            viewModel.triggerMicToggle()
                        }
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isListening) CyberGreen.copy(alpha = 0.25f) else Color(0xFF1B2333))
                        .border(1.dp, if (isListening) CyberGreen else Color.Gray.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Toggle Mic",
                        tint = if (isListening) CyberGreen else Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // "结束" Button
                Button(
                    onClick = { viewModel.onEndButtonClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("end_conversation_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE63946),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isChinese) "结束对话" else "End Conversation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // CUJ Requirement: "点击“结束”按钮 → 再次确认 → 对话结束 → 自动保存对话内容"
    if (showEndConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEndConfirmDialog() },
            title = {
                Text(
                    text = if (isChinese) "结束本次对话？" else "End Conversation?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = if (isChinese)
                        "确定要结束本次对话吗？确认后对话将立即结束，且全部内容将自动保存至本地神经记忆库中！"
                    else
                        "Confirm ending this voice conversation? All dialogue will be automatically saved to your local neural memory archive.",
                    color = Color(0xFFC7D0DC)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmEndAndSaveConversation() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("confirm_end_button")
                ) {
                    Text(
                        text = if (isChinese) "确认结束并保存" else "Confirm & Save",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissEndConfirmDialog() },
                    modifier = Modifier.testTag("cancel_end_button")
                ) {
                    Text(
                        text = if (isChinese) "继续对话" else "Cancel",
                        color = Color.LightGray
                    )
                }
            },
            containerColor = Color(0xFF161E2E),
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun AiOrbVisualizer(
    isListening: Boolean,
    isSpeaking: Boolean,
    aiVoiceState: AiVoiceState,
    rmsLevel: Float,
    onOrbClicked: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_pulse"
    )

    val currentScale = when {
        isSpeaking -> pulseScale * 1.08f
        isListening -> 1f + (rmsLevel * 0.22f)
        else -> 1f
    }

    Box(
        modifier = Modifier
            .size(130.dp)
            .scale(currentScale)
            .clickable { onOrbClicked() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension / 2f - 14.dp.toPx()

            // Outer energy aura
            val auraColor = when {
                isListening -> NeonTeal.copy(alpha = 0.25f)
                isSpeaking -> CyberCyan.copy(alpha = 0.35f)
                aiVoiceState == AiVoiceState.THINKING -> ElectricViolet.copy(alpha = 0.35f)
                else -> Color(0xFF1E2638).copy(alpha = 0.3f)
            }
            drawCircle(
                color = auraColor,
                radius = baseRadius + (rmsLevel * 20.dp.toPx())
            )

            // Inner Core gradient
            val coreBrush = Brush.radialGradient(
                colors = when {
                    isListening -> listOf(NeonTeal, Color(0xFF00B4D8), Color(0xFF052B36))
                    isSpeaking -> listOf(CyberCyan, Color(0xFF3A86FF), Color(0xFF0A192F))
                    aiVoiceState == AiVoiceState.THINKING -> listOf(ElectricViolet, Color(0xFF9D4EDD), Color(0xFF1E0A3C))
                    else -> listOf(Color(0xFF485A75), Color(0xFF1E2A3A), Color(0xFF0D141E))
                },
                center = center,
                radius = baseRadius
            )
            drawCircle(
                brush = coreBrush,
                radius = baseRadius
            )
        }

        // Center Icon / Graphic
        when {
            aiVoiceState == AiVoiceState.THINKING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = Color.White,
                    strokeWidth = 3.dp
                )
            }
            isSpeaking -> {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            isListening -> {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@Composable
fun VoiceStatusBadge(
    aiVoiceState: AiVoiceState,
    isListening: Boolean,
    isSpeaking: Boolean,
    isChinese: Boolean
) {
    val statusText = when {
        isSpeaking -> if (isChinese) "AI 正在发声回答…" else "AI is speaking..."
        aiVoiceState == AiVoiceState.THINKING -> if (isChinese) "AI 正在构思回答…" else "AI is synthesizing response..."
        isListening -> if (isChinese) "正在聆听中（请用嘴说话）…" else "Listening (speak into mic)..."
        else -> if (isChinese) "点击核心或麦克风开始说话" else "Tap orb or mic to speak"
    }

    val statusColor = when {
        isSpeaking -> CyberCyan
        aiVoiceState == AiVoiceState.THINKING -> ElectricViolet
        isListening -> CyberGreen
        else -> Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = statusColor.copy(alpha = 0.15f),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(statusColor, statusColor.copy(alpha = 0.5f))))
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (statusColor == Color.Gray) Color.LightGray else statusColor
        )
    }
}

@Composable
fun MessageBubble(msg: DialogMessage, isChinese: Boolean) {
    val isUser = msg.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) CyberCyan.copy(alpha = 0.22f) else Color(0xFF1B2333),
            border = if (isUser) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberCyan, NeonTeal))) else null,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = if (isUser) (if (isChinese) "你 (语音输入)" else "You (Spoken)") else "EvoAI",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isUser) CyberCyan else NeonTeal
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpokenSuggestionsBar(
    isChinese: Boolean,
    onSuggestionSelected: (String) -> Unit
) {
    val suggestions = if (isChinese) {
        listOf(
            "你如何看待人类的意识？",
            "在这片赛博空间我们能创造什么？",
            "给我讲个未来宇宙的故事吧",
            "面对不确定性，我们该如何选择？"
        )
    } else {
        listOf(
            "What is the essence of consciousness?",
            "What can we create in this digital space?",
            "Tell me a story about a distant star",
            "How do we navigate deep uncertainty?"
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isChinese) "用嘴说，或点击快速试说：" else "Speak into mic or tap vocal prompt:",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            suggestions.take(2).forEach { phrase ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF151C28),
                    modifier = Modifier.clickable { onSuggestionSelected(phrase) }
                ) {
                    Text(
                        text = "\"$phrase\"",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF86A0C2)
                    )
                }
            }
        }
    }
}
