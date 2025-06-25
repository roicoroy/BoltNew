package com.example.boltnew.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.repository.AuthRepository
import com.example.boltnew.data.repository.ProfileRepository
import com.example.boltnew.utils.ImageUtils
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _profileState = MutableStateFlow<RequestState<Profile>>(RequestState.Idle)
    val profileState: StateFlow<RequestState<Profile>> = _profileState.asStateFlow()
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = RequestState.Loading
                
                // First try to get profile from auth repository (Strapi API)
                val authResult = authRepository.getUserProfile()
                
                if (authResult.isSuccess) {
                    val profile = authResult.getOrThrow()
                    _profileState.value = RequestState.Success(profile)
                    
                    // Cache the profile locally
                    try {
                        profileRepository.insertProfile(profile)
                    } catch (e: Exception) {
                        // Ignore cache errors, we have the data from API
                    }
                } else {
                    // Fallback to local profile repository
                    profileRepository.getProfile()
                        .onStart { 
                            if (_profileState.value !is RequestState.Loading) {
                                _profileState.value = RequestState.Loading
                            }
                        }
                        .catch { exception ->
                            _profileState.value = RequestState.Error("Failed to load profile: ${exception.message}")
                        }
                        .collect { profile ->
                            _profileState.value = if (profile != null) {
                                RequestState.Success(profile)
                            } else {
                                RequestState.Error("Profile not found. Please ensure you're logged in.")
                            }
                        }
                }
            } catch (e: Exception) {
                _profileState.value = RequestState.Error("Failed to load profile: ${e.message}")
            }
        }
    }
    
    fun updateAvatar(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUpdatingAvatar = true)
                
                val savedPath = ImageUtils.saveImageToInternalStorage(context, imageUri)
                profileRepository.updateAvatar(savedPath, savedPath)
                
                _uiState.value = _uiState.value.copy(
                    isUpdatingAvatar = false,
                    operationMessage = "Avatar updated successfully"
                )
                
                // Reload profile to show updated avatar
                loadUserProfile()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdatingAvatar = false,
                    operationMessage = "Failed to update avatar: ${e.message}"
                )
            }
        }
    }
    
    fun refreshProfile() {
        loadUserProfile()
    }
    
    fun clearOperationMessage() {
        _uiState.value = _uiState.value.copy(operationMessage = null)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun retryLoadProfile() {
        loadUserProfile()
    }
}

data class ProfileUiState(
    val operationMessage: String? = null,
    val isUpdatingAvatar: Boolean = false
)