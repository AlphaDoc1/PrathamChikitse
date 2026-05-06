package com.example.health.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.TriageResult
import com.example.health.data.repository.EmergencyRepository
import com.example.health.data.repository.HospitalRepository
import com.example.health.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val userPrefsRepo: UserPreferencesRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                text = "I'm your emergency triage assistant. Describe the emergency situation and I'll provide immediate guidance.\n\nExamples: \"snake bite\", \"person fainted\", \"nearest hospital\"",
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
            val isHospitalQuery = lowerText.contains("hospital") || lowerText.contains("clinic") || lowerText.contains("ಆಸ್ಪತ್ರೆ")
            
            if (isHospitalQuery) {
                val lat = userPrefsRepo.lastLat.first().toDouble()
                val lng = userPrefsRepo.lastLng.first().toDouble()
                
                val responseText = if (lat != 0.0 && lng != 0.0) {
                    val hospitals = hospitalRepo.getHospitalsForLocation(lat, lng).take(3)
                    val listStr = hospitals.joinToString("\n") { "- ${it.name} (${hospitalRepo.getHospitalsForLocation(lat, lng).find { h -> h.id == it.id }?.let { h -> h.city }})" }
                    "Here are the top hospitals near your location:\n$listStr"
                } else {
                    val hospitals = hospitalRepo.getHospitalsForLocation(12.9716, 77.5946).take(3)
                    val listStr = hospitals.joinToString("\n") { "- ${it.name} (${it.city})" }
                    "Location services are unavailable. Here are the top renowned hospitals in Bangalore:\n$listStr\n\n(Please enable location in Settings or grant permissions for accurate nearby suggestions)"
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
}
