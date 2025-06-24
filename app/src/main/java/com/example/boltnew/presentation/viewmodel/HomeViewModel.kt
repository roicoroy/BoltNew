package com.example.boltnew.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.Product
import com.example.boltnew.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        initializeData()
        loadProducts()
    }
    
    private fun initializeData() {
        viewModelScope.launch {
            try {
                productRepository.initializeData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to initialize data: ${e.message}"
                )
            }
        }
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            try {
                productRepository.getAllProducts().collect { products ->
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load products: ${e.message}"
                )
            }
        }
    }
    
    fun refreshProducts() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadProducts()
    }
}

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)