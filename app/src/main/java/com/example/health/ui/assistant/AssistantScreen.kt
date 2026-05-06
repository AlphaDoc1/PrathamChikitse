package com.example.health.ui.assistant

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.R
import com.example.health.ui.theme.HealthThemeExtras

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantScreen(
    onBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToHospitals: () -> Unit,
    viewModel: AssistantViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.assistant_title), style = MaterialTheme.typography.titleMedium)
                        Text("Offline • Keyword Engine", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    if (msg.isUser) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Card(
                                shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary)
                            ) {
                                Text(msg.text, Modifier.padding(12.dp), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(28.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), Alignment.Center) {
                                    Icon(Icons.Filled.SmartToy, null, Modifier.size(16.dp), MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("Assistant", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(4.dp))
                            Card(
                                shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(msg.text, style = MaterialTheme.typography.bodyMedium)

                                    msg.triageResult?.let { result ->
                                        if (result.matchedCategory != null) {
                                            Spacer(Modifier.height(12.dp))
                                            // Urgency indicator
                                            val urgencyColor = when {
                                                result.urgencyLevel >= 4 -> HealthThemeExtras.colors.emergency
                                                result.urgencyLevel >= 3 -> HealthThemeExtras.colors.warning
                                                else -> HealthThemeExtras.colors.safe
                                            }
                                            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(urgencyColor.copy(0.1f))) {
                                                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Text("${result.matchedCategory.icon} ", style = MaterialTheme.typography.titleMedium)
                                                    Text("${result.matchedCategory.name} • Urgency: ${result.urgencyLevel}/5", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = urgencyColor)
                                                }
                                            }

                                            // Immediate actions
                                            if (result.immediateActions.isNotEmpty()) {
                                                Spacer(Modifier.height(8.dp))
                                                Text("Immediate Steps:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                                result.immediateActions.forEachIndexed { i, step ->
                                                    Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 2.dp))
                                                }
                                            }

                                            // CTA buttons
                                            Spacer(Modifier.height(12.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                FilledTonalButton(onClick = { onNavigateToCategory(result.matchedCategory.id) }, modifier = Modifier.weight(1f)) {
                                                    Icon(Icons.Filled.MenuBook, null, Modifier.size(16.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Full Guide", style = MaterialTheme.typography.labelSmall)
                                                }
                                                if (result.callEmergency) {
                                                    Button(
                                                        onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) },
                                                        modifier = Modifier.weight(1f),
                                                        colors = ButtonDefaults.buttonColors(HealthThemeExtras.colors.emergency)
                                                    ) {
                                                        Icon(Icons.Filled.Phone, null, Modifier.size(16.dp))
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("Call 108", style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                            }
                                            FilledTonalButton(onClick = onNavigateToHospitals, modifier = Modifier.fillMaxWidth()) {
                                                Icon(Icons.Filled.LocalHospital, null, Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Find Hospitals")
                                            }
                                        } else if (result.callEmergency) {
                                            Spacer(Modifier.height(8.dp))
                                            Button(
                                                onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))) },
                                                colors = ButtonDefaults.buttonColors(HealthThemeExtras.colors.emergency),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(Icons.Filled.Phone, null); Spacer(Modifier.width(4.dp)); Text("Call 108 Now")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Input bar
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.updateInput(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.type_message)) },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(
                        onClick = { viewModel.sendMessage() },
                        enabled = inputText.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Send")
                    }
                }
            }
        }
    }
}
