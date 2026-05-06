package com.example.health.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val highContrast by viewModel.highContrast.collectAsStateWithLifecycle()
    val largeText by viewModel.largeText.collectAsStateWithLifecycle()
    val seniorMode by viewModel.seniorMode.collectAsStateWithLifecycle()
    val vibrationCues by viewModel.vibrationCues.collectAsStateWithLifecycle()
    val voiceFirstMode by viewModel.voiceFirstMode.collectAsStateWithLifecycle()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Appearance section
            SettingsSection("Appearance") {
                SettingsClickItem(Icons.Filled.Palette, "Theme", themeMode.replaceFirstChar { it.uppercase() }) { showThemeDialog = true }
            }

            // General section
            SettingsSection("General") {
                val langs = mapOf("en" to "English", "hi" to "हिन्दी", "gu" to "ગુજરાતી", "mr" to "मराठी", "ta" to "தமிழ்")
                SettingsClickItem(Icons.Filled.Language, "Language", langs[language] ?: "English") { showLanguageDialog = true }
            }

            // Accessibility section
            SettingsSection("Accessibility") {
                SettingsToggleItem(Icons.Filled.Contrast, "High Contrast", "Increase contrast for better visibility", highContrast) { viewModel.setHighContrast(it) }
                SettingsToggleItem(Icons.Filled.TextFields, "Large Text", "Increase text size throughout app", largeText) { viewModel.setLargeText(it) }
                SettingsToggleItem(Icons.Filled.Elderly, "Senior Mode", "Simplified interface with larger elements", seniorMode) { viewModel.setSeniorMode(it) }
                SettingsToggleItem(Icons.Filled.Vibration, "Vibration Cues", "Haptic feedback for important actions", vibrationCues) { viewModel.setVibrationCues(it) }
                SettingsToggleItem(Icons.Filled.RecordVoiceOver, "Voice-First Mode", "Automatically read emergency steps aloud", voiceFirstMode) { viewModel.setVoiceFirstMode(it) }
            }

            // About section
            SettingsSection("About") {
                SettingsInfoItem(Icons.Filled.Info, "Version", "1.0.0")
                SettingsInfoItem(Icons.Filled.Shield, "Medical Disclaimer", "Reviewed by Dr. Rajesh Kumar")
                SettingsInfoItem(Icons.Filled.Storage, "Data", "All data stored offline on device")
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Choose Theme") },
            text = {
                Column {
                    listOf("system" to "System Default", "light" to "Light", "dark" to "Dark").forEach { (value, label) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = themeMode == value, onClick = { viewModel.setThemeMode(value); showThemeDialog = false })
                            Spacer(Modifier.width(8.dp))
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton({ showThemeDialog = false }) { Text("Cancel") } }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text("Choose Language") },
            text = {
                Column {
                    listOf("en" to "English", "hi" to "हिन्दी", "gu" to "ગુજરાતી", "mr" to "मराठी", "ta" to "தமிழ்").forEach { (code, name) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = language == code, onClick = { viewModel.setLanguage(code); showLanguageDialog = false })
                            Spacer(Modifier.width(8.dp))
                            Text(name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = { TextButton({ showLanguageDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(Modifier.padding(vertical = 4.dp), content = content)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(24.dp), MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked, onToggle)
    }
}

@Composable
private fun SettingsClickItem(icon: ImageVector, title: String, value: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(24.dp), MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        TextButton(onClick) { Text(value) }
    }
}

@Composable
private fun SettingsInfoItem(icon: ImageVector, title: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(24.dp), MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
