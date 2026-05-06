package com.example.health.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.health.data.model.EmergencyCategory
import com.example.health.data.repository.EmergencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val categories: List<EmergencyCategory> = emptyList(),
    val recentCategories: List<EmergencyCategory> = emptyList(),
    val topCategories: List<EmergencyCategory> = emptyList(),
    val bookmarkedIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val emergencyRepo: EmergencyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = combine(
        _uiState,
        emergencyRepo.bookmarks,
        emergencyRepo.recents,
        emergencyRepo.viewCounts
    ) { state, bookmarks, recents, viewCounts ->
        val categories = state.categories
        val recentCats = recents.take(5).mapNotNull { id -> categories.find { it.id == id } }
        val topCats = viewCounts.entries
            .sortedByDescending { it.value }
            .take(5)
            .mapNotNull { (id, _) -> categories.find { it.id == id } }

        state.copy(
            bookmarkedIds = bookmarks,
            recentCategories = recentCats,
            topCategories = topCats.ifEmpty {
                categories.filter { it.severityLevel.priority >= 3 }.take(5)
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        loadData()
    }

    private fun loadData() {
        emergencyRepo.getCategories().fold(
            onSuccess = { cats ->
                _uiState.value = HomeUiState(
                    categories = cats,
                    isLoading = false
                )
            },
            onFailure = { err ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = err.message ?: "Failed to load data"
                )
            }
        )
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredCategories(): List<EmergencyCategory> {
        val query = _searchQuery.value
        return if (query.isBlank()) _uiState.value.categories
        else emergencyRepo.searchCategories(query)
    }

    fun toggleBookmark(id: String) {
        viewModelScope.launch { emergencyRepo.toggleBookmark(id) }
    }
}
