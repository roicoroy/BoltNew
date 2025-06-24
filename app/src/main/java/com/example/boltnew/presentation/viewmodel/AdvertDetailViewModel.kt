package com.example.boltnew.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.Advert
import com.example.boltnew.data.repository.AdvertRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvertDetailViewModel(
    private val advertRepository: AdvertRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AdvertDetailUiState())
    val uiState: StateFlow<AdvertDetailUiState> = _uiState.asStateFlow()
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAdvert(advertId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val advert = advertRepository.getAdvertById(advertId)
                
                _uiState.value = _uiState.value.copy(
                    advert = advert,
                    isLoading = false,
                    error = if (advert == null) "Advert not found" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load advert: ${e.message}"
                )
            }
        }
    }
    
    fun contactAdvertiser() {
        // TODO: Implement contact functionality
        val advert = _uiState.value.advert
        if (advert != null) {
            // Contact logic here (email, phone, etc.)
        }
    }
}

data class AdvertDetailUiState(
    val advert: Advert? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)