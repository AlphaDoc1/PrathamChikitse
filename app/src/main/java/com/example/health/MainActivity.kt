package com.example.health

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.data.repository.EmergencyRepository
import com.example.health.data.repository.HospitalRepository
import com.example.health.data.repository.LearningRepository
import com.example.health.navigation.AppNavGraph
import com.example.health.ui.onboarding.OnboardingViewModel
import com.example.health.ui.settings.SettingsViewModel
import com.example.health.ui.theme.HealthTheme
import com.example.health.util.LocaleHelper
import com.example.health.util.TTSManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var emergencyRepo: EmergencyRepository
    @Inject lateinit var hospitalRepo: HospitalRepository
    @Inject lateinit var learningRepo: LearningRepository
    @Inject lateinit var ttsManager: TTSManager

    // Called before setContent — applies the saved locale so all stringResource() calls
    // resolve in the correct language immediately on this activity instance.
    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.wrap(newBase, lang))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVm: SettingsViewModel = hiltViewModel()
            val themeMode by settingsVm.themeMode.collectAsStateWithLifecycle()
            val language by settingsVm.language.collectAsStateWithLifecycle()
            val isDarkTheme = when (themeMode) {
                "dark"  -> true
                "light" -> false
                else    -> isSystemInDarkTheme()
            }

            // React to language changes emitted from DataStore.
            // 1. Persist to SharedPreferences so attachBaseContext reads it next time.
            // 2. Clear all JSON caches so the next load picks the translated file.
            // 3. Re-init TTS with the new locale.
            // 4. Recreate the activity ONCE — attachBaseContext will apply the locale.
            LaunchedEffect(language) {
                val persisted = LocaleHelper.getPersistedLanguage(applicationContext)
                if (persisted != language) {
                    // Persist first so attachBaseContext sees it on the next create.
                    LocaleHelper.persistLanguage(applicationContext, language)

                    // Clear caches so JSON is reloaded in the new language.
                    emergencyRepo.clearCache()
                    hospitalRepo.clearCache()
                    learningRepo.clearCache()

                    // Re-init TTS so it speaks in the correct locale.
                    ttsManager.shutdown()

                    // One clean recreate — no loop because persisted == language now.
                    recreate()
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