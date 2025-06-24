package com.example.boltnew.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.User
import com.example.boltnew.data.repository.UserRepository
import com.example.boltnew.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                userRepository.getUserProfile().collect { user ->
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load profile: ${e.message}"
                )
            }
        }
    }
    
    fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        address: String,
        dateOfBirth: LocalDate
    ) {
        viewModelScope.launch {
            try {
                val currentUser = _uiState.value.user
                val updatedUser = User(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    address = address,
                    dateOfBirth = dateOfBirth,
                    avatarPath = currentUser?.avatarPath
                )
                
                userRepository.updateUserProfile(updatedUser)
                
                _uiState.value = _uiState.value.copy(
                    isEditing = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update profile: ${e.message}"
                )
            }
        }
    }
    
    fun updateAvatar(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val savedPath = ImageUtils.saveImageToInternalStorage(context, imageUri)
                userRepository.updateAvatarPath(savedPath)
                
                _uiState.value = _uiState.value.copy(
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update avatar: ${e.message}"
                )
            }
        }
    }
    
    fun setEditing(isEditing: Boolean) {
        _uiState.value = _uiState.value.copy(isEditing = isEditing)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val error: String? = null
)