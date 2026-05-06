package com.example.health.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.health.ui.theme.HealthThemeExtras
import com.example.health.ui.theme.MedicalTeal
import com.example.health.ui.theme.MedicalTealLight
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (visible) 1f else 0.5f, spring(Spring.DampingRatioMediumBouncy), label = "s")
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(800), label = "a")
    LaunchedEffect(Unit) { visible = true; delay(2000); onFinished() }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MedicalTeal, MedicalTealLight))), Alignment.Center) {
        Column(Modifier.scale(scale).alpha(alpha), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.HealthAndSafety, null, Modifier.size(80.dp), Color.White)
            Spacer(Modifier.height(16.dp))
            Text("Pratham Chikitse", style = MaterialTheme.typography.displayMedium, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Emergency First-Aid Guide", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(0.8f))
        }
    }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    var v by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { v = true }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        AnimatedVisibility(v, enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.HealthAndSafety, null, Modifier.size(72.dp), MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(24.dp))
                Text("Welcome to\nPratham Chikitse", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(16.dp))
                Text("Your offline emergency first-aid companion.\nStep-by-step guidance during critical situations.", style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                listOf("🚑" to "15+ emergency guides", "📱" to "Works completely offline", "🏥" to "Find nearby hospitals", "🤖" to "AI emergency triage").forEach { (icon, text) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(icon, fontSize = 20.sp); Spacer(Modifier.width(12.dp))
                        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(48.dp))
        Button(onNext, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text("Get Started", style = MaterialTheme.typography.titleMedium); Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
        }
    }
}

@Composable
fun LanguageScreen(selected: String, onSelect: (String) -> Unit, onNext: () -> Unit, onSkip: () -> Unit) {
    val langs = listOf("en" to "English", "hi" to "हिन्दी", "gu" to "ગુજરાતી", "mr" to "मराठी", "ta" to "தமிழ்")
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Spacer(Modifier.height(48.dp))
        Text("Choose Language", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Select your preferred language", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        langs.forEach { (code, name) ->
            val isSel = selected == code
            Card(onClick = { onSelect(code) }, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).then(if (isSel) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier),
                shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(0.3f) else MaterialTheme.colorScheme.surfaceContainerLow)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    if (isSel) Icon(Icons.Filled.Check, "Selected", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Button(onNext, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) { Text("Continue") }
        Spacer(Modifier.height(8.dp))
        TextButton(onSkip, Modifier.fillMaxWidth()) { Text("Skip for now") }
    }
}

@Composable
fun PermissionsScreen(onNext: () -> Unit, onSkip: () -> Unit, viewModel: OnboardingViewModel = androidx.hilt.navigation.compose.hiltViewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true || 
                      permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                val client = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
                client.lastLocation.addOnSuccessListener { location ->
                    if (location != null) viewModel.saveLocation(location.latitude, location.longitude)
                    onNext()
                }.addOnFailureListener { onNext() }
            } catch (e: SecurityException) {
                onNext()
            }
        } else {
            onNext()
        }
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Spacer(Modifier.height(48.dp))
        Text("Permissions", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("These help provide better emergency assistance", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        PermCard(Icons.Filled.Phone, "Phone Calls", "Directly call emergency services (108)")
        Spacer(Modifier.height(12.dp))
        PermCard(Icons.Filled.LocationOn, "Location", "Find nearby hospitals sorted by distance")
        Spacer(Modifier.weight(1f))
        Button({ 
            launcher.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }, Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) { Text("Grant Permissions") }
        Spacer(Modifier.height(8.dp))
        TextButton(onSkip, Modifier.fillMaxWidth()) { Text("Skip - I'll set up later") }
    }
}

@Composable
private fun PermCard(icon: ImageVector, title: String, desc: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun DisclaimerScreen(body: String, reviewedBy: String, onAccept: () -> Unit) {
    var checked by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Spacer(Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Shield, null, Modifier.size(32.dp), HealthThemeExtras.colors.emergency)
            Spacer(Modifier.width(12.dp))
            Text("Medical Disclaimer", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.weight(1f).fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text(body, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                if (reviewedBy.isNotBlank()) {
                    Spacer(Modifier.height(16.dp))
                    Text("Reviewed by: $reviewedBy", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked, { checked = it })
            Spacer(Modifier.width(8.dp))
            Text("I understand and accept this disclaimer", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.clickable { checked = !checked })
        }
        Spacer(Modifier.height(16.dp))
        Button(onAccept, Modifier.fillMaxWidth().height(56.dp), enabled = checked, shape = RoundedCornerShape(16.dp)) { Text("Accept & Continue", style = MaterialTheme.typography.titleMedium) }
    }
}

@Composable
fun EmergencyContactsSetupScreen(onSave: (String) -> Unit, onSkip: () -> Unit) {
    val contacts = remember { mutableStateListOf<String>() }
    var input by remember { mutableStateOf("") }
    val fm = LocalFocusManager.current
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp)) {
        Spacer(Modifier.height(48.dp))
        Text("Emergency Contacts", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Add people to contact during emergencies", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(input, { input = it }, Modifier.weight(1f), label = { Text("Phone number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (input.length >= 10) { contacts.add(input); input = ""; fm.clearFocus() } }),
                singleLine = true, shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.width(8.dp))
            IconButton({ if (input.length >= 10) { contacts.add(input); input = "" } }) { Icon(Icons.Filled.Add, "Add") }
        }
        Spacer(Modifier.height(16.dp))
        contacts.forEachIndexed { i, c ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Phone, null, tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp))
                    Text(c, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    IconButton({ contacts.removeAt(i) }) { Icon(Icons.Filled.Delete, "Remove", tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Button({ onSave(contacts.joinToString(",")) }, Modifier.fillMaxWidth().height(56.dp), enabled = contacts.isNotEmpty(), shape = RoundedCornerShape(16.dp)) { Text("Save & Continue") }
        Spacer(Modifier.height(8.dp))
        TextButton(onSkip, Modifier.fillMaxWidth()) { Text("Skip - I'll add later") }
    }
}
