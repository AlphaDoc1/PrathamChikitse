package com.example.health.ui.assistant

import androidx.lifecycle.ViewModel
import com.example.health.data.model.TriageResult
import com.example.health.data.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class AssistantMessage(
    val text: String,
    val isUser: Boolean,
    val triageResult: TriageResult? = null
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val emergencyRepo: EmergencyRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AssistantMessage>>(
        listOf(
            AssistantMessage(
                text = "I'm your emergency triage assistant. Describe the emergency situation and I'll provide immediate guidance.\n\nExamples: \"snake bite\", \"person fainted\", \"severe bleeding\"",
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
        val result = emergencyRepo.triageQuery(text)
        val responseMsg = AssistantMessage(
            text = result.message,
            isUser = false,
            triageResult = result
        )

        _messages.value = _messages.value + userMsg + responseMsg
        _inputText.value = ""
    }
}
