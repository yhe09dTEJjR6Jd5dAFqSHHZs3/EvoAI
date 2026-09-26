package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ConversationEntity
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isChinese by viewModel.isChinese.collectAsStateWithLifecycle()
    val promptText by viewModel.promptText.collectAsStateWithLifecycle()
    val conversations by viewModel.allConversations.collectAsStateWithLifecycle()
    val evoState by viewModel.evolutionState.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp)
                ) {
                    TopHeaderSection(
                        isChinese = isChinese,
                        evoState = evoState,
                        onToggleLanguage = { viewModel.setLanguage(!isChinese) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    // Prompt Input Section
                    PromptInputCard(
                        prompt = promptText,
                        isChinese = isChinese,
                        onPromptChanged = { viewModel.updatePrompt(it) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Action Buttons: "开始" & "进化"
                    ActionButtonsSection(
                        isChinese = isChinese,
                        hasSavedConversations = conversations.isNotEmpty(),
                        onStartClicked = { viewModel.startVoiceConversation() },
                        onEvolveClicked = { viewModel.startEvolution() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Neural Architecture & Evolution Stats
                    NeuralArchitectureCard(
                        evoState = evoState,
                        savedCount = conversations.size,
                        isChinese = isChinese
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Saved Memory History List Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = NeonTeal,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isChinese) "已存对话记忆库" else "Saved Memory Archive",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonTeal.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${conversations.size}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = NeonTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (conversations.isEmpty()) {
                item {
                    EmptyHistoryCard(isChinese = isChinese)
                }
            } else {
                items(conversations, key = { it.id }) { conv ->
                    ConversationHistoryItem(
                        conversation = conv,
                        isChinese = isChinese,
                        onClick = { viewModel.selectConversationForDetail(conv) },
                        onDelete = { viewModel.deleteConversation(conv.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TopHeaderSection(
    isChinese: Boolean,
    evoState: com.example.data.EvolutionStateEntity?,
    onToggleLanguage: () -> Unit
) {
    val gen = evoState?.generation ?: 1
    val genTitle = if (isChinese) (evoState?.generationTitleZh ?: "原初思维体") else (evoState?.generationTitleEn ?: "Proto-Mind")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
    ) {
        // Hero Background Artwork
        Image(
            painter = painterResource(id = R.drawable.evo_hero_1790385679330),
            contentDescription = "EvoAI Core Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay for sleek readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color(0xFF0A0E17).copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Generation Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyberCyan.copy(alpha = 0.18f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberCyan, NeonTeal)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CyberGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isChinese) "第 $gen 代 · $genTitle" else "Gen $gen • $genTitle",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                }

                // Language Switch & Offline Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF161B22).copy(alpha = 0.8f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isChinese) "完全离线可用" else "Offline Ready",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onToggleLanguage,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Switch language",
                            modifier = Modifier.size(14.dp),
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isChinese) "EN" else "中文",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                }
            }

            // Title & Tagline
            Column {
                Text(
                    text = if (isChinese) "EvoAI 进化智心" else "EvoAI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = if (isChinese) "离线语音伴侣 · 自主神经进化" else "Offline Voice Companion & Autonomous Neural Evolution",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB0BAC5)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PromptInputCard(
    prompt: String,
    isChinese: Boolean,
    onPromptChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberCyan.copy(alpha = 0.4f), ElectricViolet.copy(alpha = 0.3f))))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isChinese) "提示词（AI 人格 / 角色设定）" else "System Prompt / AI Persona",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = onPromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prompt_input_field"),
                placeholder = {
                    Text(
                        text = if (isChinese) "输入提示词（如：苏格拉底式的哲学家、科幻旅伴…）" else "Enter prompt (e.g. Philosophical sage, Cyber companion...)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Preset Chips
            Text(
                text = if (isChinese) "快捷提示词预设：" else "Quick Presets:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            val presets = if (isChinese) {
                listOf(
                    "哲学家（苏格拉底式思辨）",
                    "赛博旅伴（冷幽默与未来科技）",
                    "未来科学家（严谨前沿探索）",
                    "浪漫诗人（诗意与想象力）",
                    "知心密友（温暖倾听共情）"
                )
            } else {
                listOf(
                    "Philosophical Sage (Socratic inquiry)",
                    "Cyberpunk Companion (Witty sci-fi)",
                    "Futuristic Scientist (Empirical)",
                    "Poetic Dreamer (Creative metaphors)",
                    "Warm Confidant (Empathetic listener)"
                )
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = prompt == preset
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CyberCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberCyan, NeonTeal))) else null,
                        modifier = Modifier
                            .clickable { onPromptChanged(preset) }
                            .padding(0.dp)
                    ) {
                        Text(
                            text = preset.substringBefore("（").substringBefore(" ("),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) CyberCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    isChinese: Boolean,
    hasSavedConversations: Boolean,
    onStartClicked: () -> Unit,
    onEvolveClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // "开始" (Start) Button
        Button(
            onClick = onStartClicked,
            modifier = Modifier
                .weight(1.3f)
                .height(58.dp)
                .testTag("start_conversation_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberCyan,
                contentColor = Color(0xFF002B31)
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isChinese) "开始" else "Start",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = if (isChinese) "语音对话（用嘴）" else "Voice Chat",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 12.sp
                    )
                }
            }
        }

        // "进化" (Evolve) Button
        Button(
            onClick = onEvolveClicked,
            modifier = Modifier
                .weight(1f)
                .height(58.dp)
                .testTag("evolve_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricViolet,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CyberAmber,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isChinese) "进化" else "Evolve",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = if (isChinese) "AI开始进化" else "Neural Leap",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberAmber,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NeuralArchitectureCard(
    evoState: com.example.data.EvolutionStateEntity?,
    savedCount: Int,
    isChinese: Boolean
) {
    val synapses = evoState?.synapseCount ?: 1200
    val vocab = evoState?.vocabularySize ?: 850
    val empathy = evoState?.empathyLevel ?: 30
    val logic = evoState?.logicLevel ?: 35
    val creativity = evoState?.creativityLevel ?: 30
    val traits = evoState?.unlockedTraits?.split(",")?.map { it.trim() } ?: listOf("Curiosity", "Basic Reasoning")

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = CyberAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isChinese) "神经架构状态" else "Neural Architecture Stats",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${synapses} ${if (isChinese) "突触" else "Synapses"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat bars
            StatProgressBar(label = if (isChinese) "逻辑深度 (Logic)" else "Logic", value = logic, color = CyberCyan)
            Spacer(modifier = Modifier.height(8.dp))
            StatProgressBar(label = if (isChinese) "共情指数 (Empathy)" else "Empathy", value = empathy, color = Color(0xFFFF69B4))
            Spacer(modifier = Modifier.height(8.dp))
            StatProgressBar(label = if (isChinese) "创造力 (Creativity)" else "Creativity", value = creativity, color = CyberAmber)

            Spacer(modifier = Modifier.height(14.dp))

            // Unlocked Traits
            Text(
                text = if (isChinese) "已解锁认知特质：" else "Unlocked Cognitive Traits:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                traits.take(3).forEach { trait ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ElectricViolet.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = trait,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD4B2FF)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatProgressBar(label: String, value: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$value%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun EmptyHistoryCard(isChinese: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 640.dp)
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubble,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (isChinese) "暂无保存的对话" else "No saved conversations yet",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isChinese)
                    "输入提示词后点击“开始”，用嘴与 AI 对话，结束后对话将自动保存并可用于“进化”！"
                else
                    "Enter a prompt and tap 'Start' to talk with voice. Ended conversations automatically save to memory archive for evolution!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ConversationHistoryItem(
    conversation: ConversationEntity,
    isChinese: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date(conversation.endTime))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 640.dp)
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberCyan.copy(alpha = 0.2f), Color.Transparent)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = CyberCyan.copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.personaName.ifBlank { "AI Persona" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = conversation.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (conversation.learnedConcepts.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isChinese) "提炼概念：${conversation.learnedConcepts}" else "Concepts: ${conversation.learnedConcepts}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeonTeal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
