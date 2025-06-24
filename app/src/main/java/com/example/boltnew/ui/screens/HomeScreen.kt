package com.example.boltnew.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.boltnew.presentation.viewmodel.HomeViewModel
import com.example.boltnew.ui.components.AdvertCard
import com.example.boltnew.utils.DisplayResult
import com.example.boltnew.utils.RequestState
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAdvertClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val advertsState by viewModel.advertsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Adverts",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.searchAdverts(it)
            },
            label = { Text("Search adverts...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        // Category Filter
        categoriesState.DisplayResult(
            onLoading = {
                // Categories loading is silent
            },
            onError = { 
                // Categories error is silent
            },
            onSuccess = { categories ->
                if (categories.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                onClick = { 
                                    viewModel.filterByCategory(null)
                                    viewModel.clearSelectedCategory()
                                },
                                label = { Text("All") },
                                selected = uiState.selectedCategory == null
                            )
                        }
                        items(categories) { category ->
                            FilterChip(
                                onClick = { viewModel.filterByCategory(category) },
                                label = { Text(category) },
                                selected = uiState.selectedCategory == category
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        )
        
        // Adverts Content
        advertsState.DisplayResult(
            modifier = Modifier.fillMaxSize(),
            onLoading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading adverts...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            onError = { errorMessage ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshAdverts() }) {
                            Text("Retry")
                        }
                    }
                }
            },
            onSuccess = { adverts ->
                if (adverts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No adverts found for \"$searchQuery\""
                                } else if (uiState.selectedCategory != null) {
                                    "No adverts found in \"${uiState.selectedCategory}\" category"
                                } else {
                                    "No adverts available"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            if (searchQuery.isNotBlank() || uiState.selectedCategory != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = {
                                        searchQuery = ""
                                        viewModel.clearSearchQuery()
                                        viewModel.clearSelectedCategory()
                                        viewModel.refreshAdverts()
                                    }
                                ) {
                                    Text("Show All Adverts")
                                }
                            }
                        }
                    }
                } else {
                    // Advert List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(adverts) { advert ->
                            AdvertCard(
                                advert = advert,
                                onClick = { onAdvertClick(advert.id) }
                            )
                        }
                    }
                }
            }
        )
    }
}