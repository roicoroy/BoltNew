package com.example.boltnew.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.Profile
import com.example.boltnew.data.repository.ProfileRepository
import com.example.boltnew.utils.ImageUtils
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _profileState = MutableStateFlow<RequestState<Profile>>(RequestState.Idle)
    val profileState: StateFlow<RequestState<Profile>> = _profileState.asStateFlow()
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getProfile()
                .onStart { 
                    _profileState.value = RequestState.Loading 
                }
                .catch { exception ->
                    _profileState.value = RequestState.Error("Failed to load profile: ${exception.message}")
                }
                .collect { profile ->
                    _profileState.value = if (profile != null) {
                        RequestState.Success(profile)
                    } else {
                        RequestState.Error("Profile not found")
                    }
                }
        }
    }
    
    fun updateAvatar(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val savedPath = ImageUtils.saveImageToInternalStorage(context, imageUri)
                profileRepository.updateAvatar(savedPath, savedPath)
                
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Avatar updated successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Failed to update avatar: ${e.message}"
                )
            }
        }
    }
    
    fun clearOperationMessage() {
        _uiState.value = _uiState.value.copy(operationMessage = null)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun retryLoadProfile() {
        loadProfile()
    }
}

data class ProfileUiState(
    val operationMessage: String? = null
)