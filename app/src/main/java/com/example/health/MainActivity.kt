package com.example.health

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.navigation.AppNavGraph
import com.example.health.ui.onboarding.OnboardingViewModel
import com.example.health.ui.settings.SettingsViewModel
import com.example.health.ui.theme.HealthTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val themeMode by settingsVm.themeMode.collectAsStateWithLifecycle()
            val language by settingsVm.language.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            androidx.compose.runtime.LaunchedEffect(language) {
                try {
                    val locale = java.util.Locale(language)
                    java.util.Locale.setDefault(locale)
                    val resources = baseContext.resources
                    val config = resources.configuration
                    config.setLocale(locale)
                    @Suppress("DEPRECATION")
                    resources.updateConfiguration(config, resources.displayMetrics)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            HealthTheme(darkTheme = isDarkTheme) {
                val onboardingVm: OnboardingViewModel = hiltViewModel()
                val onboardingDone by onboardingVm.onboardingCompleted.collectAsStateWithLifecycle()
                val disclaimerDone by onboardingVm.disclaimerAccepted.collectAsStateWithLifecycle()

                AppNavGraph(
                    onboardingDone = onboardingDone,
                    disclaimerDone = disclaimerDone
                )
            }
        }
    }
}