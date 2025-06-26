package com.example.boltnew.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.advert.Advert
import com.example.boltnew.data.repository.advert.AdvertRepository
import com.example.boltnew.data.repository.advert.AdvertRepositoryImpl
import com.example.boltnew.data.repository.profile.ProfileRepository
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AdvertViewModel(
    private val advertRepository: AdvertRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _advertsState = MutableStateFlow<RequestState<List<Advert>>>(RequestState.Idle)
    val advertsState: StateFlow<RequestState<List<Advert>>> = _advertsState.asStateFlow()
    
    private val _categoriesState = MutableStateFlow<RequestState<List<String>>>(RequestState.Idle)
    val categoriesState: StateFlow<RequestState<List<String>>> = _categoriesState.asStateFlow()
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
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
                // Initialization errors are handled silently
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadAdverts() {
        viewModelScope.launch {
            advertRepository.getAllAdverts()
                .onStart { 
                    if (!_isRefreshing.value) {
                        _advertsState.value = RequestState.Loading
                    }
                }
                .catch { exception ->
                    _advertsState.value = RequestState.Error("Failed to load adverts from API: ${exception.message}")
                    _isRefreshing.value = false
                }
                .collect { adverts ->
                    _advertsState.value = RequestState.Success(adverts)
                    _isRefreshing.value = false
                }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            advertRepository.getAllCategories()
                .onStart { 
                    _categoriesState.value = RequestState.Loading 
                }
                .catch { exception ->
                    _categoriesState.value = RequestState.Error("Failed to load categories from API: ${exception.message}")
                }
                .collect { categories ->
                    _categoriesState.value = RequestState.Success(categories)
                }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterByCategory(categoryName: String?) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(selectedCategory = categoryName)
                _advertsState.value = RequestState.Loading
                
                if (categoryName == null) {
                    loadAdverts()
                } else {
                    // Find category slug by name (simplified approach)
                    val categorySlug = categoryName.lowercase().replace(" ", "-")
                    advertRepository.getAdvertsByCategory(categorySlug)
                        .onStart { 
                            _advertsState.value = RequestState.Loading 
                        }
                        .catch { exception ->
                            _advertsState.value = RequestState.Error("Failed to filter adverts from API: ${exception.message}")
                        }
                        .collect { adverts ->
                            _advertsState.value = RequestState.Success(adverts)
                        }
                }
            } catch (e: Exception) {
                _advertsState.value = RequestState.Error("Failed to filter adverts: ${e.message}")
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun searchAdverts(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(searchQuery = query)
                _advertsState.value = RequestState.Loading
                
                if (query.isBlank()) {
                    loadAdverts()
                } else {
                    advertRepository.searchAdverts(query)
                        .onStart { 
                            _advertsState.value = RequestState.Loading 
                        }
                        .catch { exception ->
                            _advertsState.value = RequestState.Error("Failed to search adverts from API: ${exception.message}")
                        }
                        .collect { adverts ->
                            _advertsState.value = RequestState.Success(adverts)
                        }
                }
            } catch (e: Exception) {
                _advertsState.value = RequestState.Error("Failed to search adverts: ${e.message}")
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshAdverts() {
        _isRefreshing.value = true
        
        // Clear current filters and search when refreshing
        _uiState.value = _uiState.value.copy(
            selectedCategory = null,
            searchQuery = ""
        )
        
        viewModelScope.launch {
            try {
                // Force refresh from API
                if (advertRepository is AdvertRepositoryImpl) {
                    val success = advertRepository.refreshFromApi()
                    if (success) {
                        loadAdverts()
                        loadCategories()
                    } else {
                        _advertsState.value = RequestState.Error("Failed to refresh from API - using cached data")
                        _isRefreshing.value = false
                    }
                } else {
                    loadAdverts()
                    loadCategories()
                }
            } catch (e: Exception) {
                _advertsState.value = RequestState.Error("Failed to refresh adverts: ${e.message}")
                _isRefreshing.value = false
            }
        }
    }
    
    fun clearSelectedCategory() {
        _uiState.value = _uiState.value.copy(selectedCategory = null)
    }
    
    fun clearSearchQuery() {
        _uiState.value = _uiState.value.copy(searchQuery = "")
    }
}

data class HomeUiState(
    val selectedCategory: String? = null,
    val searchQuery: String = ""
)