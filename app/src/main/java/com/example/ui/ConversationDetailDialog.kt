package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ConversationEntity
import com.example.data.DialogMessage
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ConversationDetailDialog(
    conversation: ConversationEntity,
    messages: List<DialogMessage>,
    isChinese: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(conversation.endTime))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isChinese) "已存对话详情" else "Conversation Memory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1B2333),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isChinese) "提示词设定：" else "Prompt Persona:",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonTeal
                        )
                        Text(
                            text = conversation.prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                }

                if (conversation.learnedConcepts.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isChinese) "提炼概念：${conversation.learnedConcepts}" else "Learned Concepts: ${conversation.learnedConcepts}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isChinese) "完整对话记录：" else "Dialogue Transcript:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        val isUser = msg.sender == "user"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isUser) CyberCyan.copy(alpha = 0.15f) else Color(0xFF121824),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = if (isUser) (if (isChinese) "你 (语音)：" else "You (Voice):") else "EvoAI：",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) CyberCyan else NeonTeal
                                )
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = if (isChinese) "关闭" else "Close", color = CyberCyan)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = if (isChinese) "删除此记忆" else "Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        containerColor = Color(0xFF161E2E),
        shape = RoundedCornerShape(18.dp)
    )
}
