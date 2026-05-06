package com.example.health.ui.assistant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.R
import com.example.health.data.model.TriageResult
import com.example.health.data.repository.EmergencyRepository
import com.example.health.data.repository.HospitalRepository
import com.example.health.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssistantMessage(
    val text: String,
    val isUser: Boolean,
    val triageResult: TriageResult? = null
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val emergencyRepo: EmergencyRepository,
    private val hospitalRepo: HospitalRepository,
    private val userPrefsRepo: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Welcome message uses the Application context so it picks up the locale
    // that was applied via LocaleHelper.wrap() in attachBaseContext.
    // Result: the message is always in the currently-selected language.
    private val _messages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                text = context.getString(R.string.assistant_welcome),
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<AssistantMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    fun updateInput(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return

        val userMsg = AssistantMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMsg
        _inputText.value = ""

        viewModelScope.launch {
            val lowerText = text.lowercase()

            // Detect hospital queries in all supported languages
            val isHospitalQuery = lowerText.containsAny(
                "hospital", "clinic", "doctor",
                // Kannada
                "ಆಸ್ಪತ್ರೆ", "ವೈದ್ಯ",
                // Hindi
                "अस्पताल", "क्लिनिक", "डॉक्टर",
                // Gujarati
                "હૉસ્પિટલ", "ક્લિનિક",
                // Marathi
                "रुग्णालय", "दवाखाना",
                // Tamil
                "மருத்துவமனை", "மருத்துவர்"
            )

            if (isHospitalQuery) {
                val lat = userPrefsRepo.lastLat.first().toDouble()
                val lng = userPrefsRepo.lastLng.first().toDouble()

                val responseText = if (lat != 0.0 && lng != 0.0) {
                    val hospitals = hospitalRepo.getHospitalsForLocation(lat, lng).take(3)
                    val listStr = hospitals.joinToString("\n") { "- ${it.name} (${it.city})" }
                    context.getString(R.string.hospitals_near_you) + "\n$listStr"
                } else {
                    val hospitals = hospitalRepo.getHospitalsForLocation(12.9716, 77.5946).take(3)
                    val listStr = hospitals.joinToString("\n") { "- ${it.name} (${it.city})" }
                    context.getString(R.string.hospitals_no_location) + "\n$listStr"
                }

                _messages.value = _messages.value + AssistantMessage(text = responseText, isUser = false)
            } else {
                val result = emergencyRepo.triageQuery(text)
                val responseMsg = AssistantMessage(
                    text = result.message,
                    isUser = false,
                    triageResult = result
                )
                _messages.value = _messages.value + responseMsg
            }
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it) }
}
