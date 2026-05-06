package com.example.health.ui.emergency

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.health.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyListScreen(
    onCategoryClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Guides") },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query, onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search emergencies...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = { if (query.isNotEmpty()) IconButton({ query = "" }) { Icon(Icons.Filled.Clear, null) } },
                singleLine = true, shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(8.dp))

            when {
                isLoading -> ShimmerEffect()
                error != null -> ErrorState(message = error ?: "Error")
                else -> {
                    val filtered = if (query.isBlank()) categories
                    else categories.filter { c -> c.name.contains(query, true) || c.keywords.any { it.contains(query, true) } }

                    LazyColumn(contentPadding = PaddingValues(bottom = 80.dp)) {
                        items(filtered, key = { it.id }) { cat ->
                            EmergencyCard(
                                category = cat,
                                isBookmarked = bookmarks.contains(cat.id),
                                onClick = { onCategoryClick(cat.id) },
                                onBookmark = { viewModel.toggleBookmark(cat.id) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).animateContentSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
