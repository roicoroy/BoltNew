package com.example.boltnew.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.Advert
import com.example.boltnew.data.repository.AdvertRepository
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdvertDetailViewModel(
    private val advertRepository: AdvertRepository
) : ViewModel() {
    
    private val _advertState = MutableStateFlow<RequestState<Advert>>(RequestState.Idle)
    val advertState: StateFlow<RequestState<Advert>> = _advertState.asStateFlow()
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadAdvert(advertId: Int) {
        viewModelScope.launch {
            try {
                _advertState.value = RequestState.Loading
                
                val advert = advertRepository.getAdvertById(advertId)
                
                _advertState.value = if (advert != null) {
                    RequestState.Success(advert)
                } else {
                    RequestState.Error("Advert not found")
                }
            } catch (e: Exception) {
                _advertState.value = RequestState.Error("Failed to load advert: ${e.message}")
            }
        }
    }
    
    fun contactAdvertiser() {
        // TODO: Implement contact functionality
        val currentState = _advertState.value
        if (currentState is RequestState.Success) {
            val advert = currentState.data
            // Contact logic here (email, phone, etc.)
        }
    }
    
    fun retryLoading(advertId: Int) {
        loadAdvert(advertId)
    }
}