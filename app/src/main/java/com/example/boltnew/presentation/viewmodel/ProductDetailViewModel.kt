package com.example.boltnew.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.Product
import com.example.boltnew.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()
    
    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val product = productRepository.getProductById(productId)
                
                _uiState.value = _uiState.value.copy(
                    product = product,
                    isLoading = false,
                    error = if (product == null) "Product not found" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load product: ${e.message}"
                )
            }
        }
    }
    
    fun addToCart() {
        // TODO: Implement add to cart functionality
        val product = _uiState.value.product
        if (product != null && product.inStock) {
            // Add to cart logic here
        }
    }
}

data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)