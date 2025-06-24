package com.example.boltnew.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.Advert
import com.example.boltnew.data.repository.AdvertRepository
import com.example.boltnew.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val advertRepository: AdvertRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        initializeData()
        loadAdverts()
        loadCategories()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeData() {
        viewModelScope.launch {
            try {
                advertRepository.initializeData()
                profileRepository.initializeDefaultProfile()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to initialize data: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAdverts() {
        viewModelScope.launch {
            try {
                advertRepository.getAllAdverts().collect { adverts ->
                    _uiState.value = _uiState.value.copy(
                        adverts = adverts,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load adverts: ${e.message}"
                )
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                advertRepository.getAllCategories().collect { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories
                    )
                }
            } catch (e: Exception) {
                // Categories loading failure shouldn't block the main content
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterByCategory(categoryName: String?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    selectedCategory = categoryName,
                    isLoading = true
                )
                
                if (categoryName == null) {
                    loadAdverts()
                } else {
                    // Find category slug by name (simplified approach)
                    val categorySlug = categoryName.lowercase().replace(" ", "-")
                    advertRepository.getAdvertsByCategory(categorySlug).collect { adverts ->
                        _uiState.value = _uiState.value.copy(
                            adverts = adverts,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to filter adverts: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun searchAdverts(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    searchQuery = query,
                    isLoading = true
                )
                
                if (query.isBlank()) {
                    loadAdverts()
                } else {
                    advertRepository.searchAdverts(query).collect { adverts ->
                        _uiState.value = _uiState.value.copy(
                            adverts = adverts,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to search adverts: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshAdverts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadAdverts()
    }
}

data class HomeUiState(
    val adverts: List<Advert> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)