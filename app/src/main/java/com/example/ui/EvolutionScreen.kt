package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonTeal

@Composable
fun EvolutionScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val isChinese by viewModel.isChinese.collectAsStateWithLifecycle()
    val step by viewModel.evolutionStep.collectAsStateWithLifecycle()
    val result by viewModel.latestEvolutionResult.collectAsStateWithLifecycle()
    val evoState by viewModel.evolutionState.collectAsStateWithLifecycle()

    BackHandler {
        if (step >= 3) {
            viewModel.completeEvolution()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "evo_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF070B12)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ElectricViolet.copy(alpha = 0.2f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ElectricViolet, CyberCyan)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyberAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isChinese) "AI 自主神经进化" else "Autonomous Neural Evolution",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quantum Synaptic Core Canvas
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotationAngle)
                    ) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val radius = size.minDimension / 2f - 16.dp.toPx()

                        // Rotating orbital ring 1
                        drawCircle(
                            brush = Brush.sweepGradient(listOf(CyberCyan, ElectricViolet, NeonTeal, CyberCyan)),
                            radius = radius,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Outer dashed points
                        for (i in 0 until 12) {
                            val angle = (i * 30f) * (Math.PI / 180f).toFloat()
                            val dotOffset = Offset(
                                center.x + (radius + 8.dp.toPx()) * kotlin.math.cos(angle),
                                center.y + (radius + 8.dp.toPx()) * kotlin.math.sin(angle)
                            )
                            drawCircle(
                                color = if (i % 2 == 0) CyberCyan else CyberAmber,
                                radius = 3.dp.toPx(),
                                center = dotOffset
                            )
                        }
                    }

                    // Pulsing Inner Sphere
                    Surface(
                        modifier = Modifier.size(110.dp),
                        shape = CircleShape,
                        color = Color(0xFF140D2B),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.radialGradient(listOf(CyberCyan, ElectricViolet)))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = if (step >= 3) CyberAmber else CyberCyan,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stage Title & Subtitle
                Text(
                    text = when (step) {
                        0 -> if (isChinese) "读取对话记忆库…" else "Accessing memory archive..."
                        1 -> if (isChinese) "解析情境知识与情感向量…" else "Parsing episodic memory & sentiment..."
                        2 -> if (isChinese) "重构神经突触拓扑架构…" else "Restructuring synaptic architecture..."
                        else -> if (isChinese) "认知跃迁完成！" else "Cognitive Leap Attained!"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (step) {
                        0 -> if (isChinese) "从已保存的对话中提炼认知模式" else "Extracting cognitive patterns from saved chats"
                        1 -> if (isChinese) "融合词汇理解与共情上下文权重" else "Synthesizing vocabulary and empathy weights"
                        2 -> if (isChinese) "自主优化逻辑深度与联想网络" else "Optimizing logic depth & associative network"
                        else -> if (isChinese) "AI 思维体已跃迁至全新演化世代" else "AI core has advanced to a higher generation"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9AA7B6),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Multi-stage Telemetry Progress
                Column(modifier = Modifier.fillMaxWidth()) {
                    LinearProgressIndicator(
                        progress = { ((step + 1) / 4f).coerceIn(0.2f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (step >= 3) CyberGreen else CyberCyan,
                        trackColor = Color(0xFF1A2233)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Milestone Card on Complete
                AnimatedVisibility(
                    visible = step >= 3,
                    enter = fadeIn(tween(400)) + slideInVertically(tween(400))
                ) {
                    result?.let { res ->
                        EvolutionSummaryCard(result = res, isChinese = isChinese)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            if (step >= 3) {
                Button(
                    onClick = { viewModel.completeEvolution() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 560.dp)
                        .height(54.dp)
                        .testTag("complete_evolution_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF002A30)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = if (isChinese) "完成进化，进入新世代" else "Accept Neural Leap",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun EvolutionSummaryCard(
    result: com.example.engine.EvolutionEngine.EvolutionResult,
    isChinese: Boolean
) {
    val updated = result.updatedState
    val genTitle = if (isChinese) updated.generationTitleZh else updated.generationTitleEn

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131926)),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(CyberCyan, ElectricViolet)))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isChinese) "已跃迁至 第 ${updated.generation} 代" else "Advanced to Gen ${updated.generation}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberCyan.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = genTitle,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Unlocked Trait
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = ElectricViolet.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyberAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isChinese) "解锁全新高阶认知特质" else "Unlocked New Cognitive Trait",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD4B2FF)
                        )
                        Text(
                            text = result.newTraitUnlocked,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat Gains
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatGainPill(
                    label = if (isChinese) "突触规模" else "Synapses",
                    value = "${updated.synapseCount}",
                    gain = "+1800"
                )
                StatGainPill(
                    label = if (isChinese) "词汇理解" else "Vocabulary",
                    value = "${updated.vocabularySize}",
                    gain = "+750"
                )
                StatGainPill(
                    label = if (isChinese) "共情深度" else "Empathy",
                    value = "${updated.empathyLevel}%",
                    gain = "+12%"
                )
            }
        }
    }
}

@Composable
fun StatGainPill(label: String, value: String, gain: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF1B2333)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = gain, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CyberGreen)
        }
    }
}
