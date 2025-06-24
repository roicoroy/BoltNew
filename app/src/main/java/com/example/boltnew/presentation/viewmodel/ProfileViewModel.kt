package com.example.boltnew.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.Profile
import com.example.boltnew.data.model.Address
import com.example.boltnew.data.model.UserAdvert
import com.example.boltnew.data.repository.ProfileRepository
import com.example.boltnew.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfile()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadProfile() {
        viewModelScope.launch {
            try {
                profileRepository.getProfile().collect { profile ->
                    _uiState.value = _uiState.value.copy(
                        profile = profile,
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
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateProfile(
        username: String,
        email: String,
        dateOfBirth: LocalDate,
        addresses: List<Address> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                val currentProfile = _uiState.value.profile
                val updatedProfile = currentProfile?.copy(
                    username = username,
                    email = email,
                    dateOfBirth = dateOfBirth,
                    addresses = addresses.ifEmpty { currentProfile.addresses }
                ) ?: Profile(
                    username = username,
                    email = email,
                    dateOfBirth = dateOfBirth,
                    addresses = addresses
                )
                
                profileRepository.updateProfile(updatedProfile)
                
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
                profileRepository.updateAvatar(savedPath, savedPath)
                
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
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun addAddress(address: Address) {
        viewModelScope.launch {
            try {
                val currentProfile = _uiState.value.profile
                if (currentProfile != null) {
                    val updatedAddresses = currentProfile.addresses + address
                    val updatedProfile = currentProfile.copy(addresses = updatedAddresses)
                    profileRepository.updateProfile(updatedProfile)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add address: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAddress(address: Address) {
        viewModelScope.launch {
            try {
                val currentProfile = _uiState.value.profile
                if (currentProfile != null) {
                    val updatedAddresses = currentProfile.addresses.map { 
                        if (it.id == address.id) address else it 
                    }
                    val updatedProfile = currentProfile.copy(addresses = updatedAddresses)
                    profileRepository.updateProfile(updatedProfile)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update address: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            try {
                val currentProfile = _uiState.value.profile
                if (currentProfile != null) {
                    val updatedAddresses = currentProfile.addresses.filter { it.id != address.id }
                    val updatedProfile = currentProfile.copy(addresses = updatedAddresses)
                    profileRepository.updateProfile(updatedProfile)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete address: ${e.message}"
                )
            }
        }
    }
    
    fun setEditing(isEditing: Boolean) {
        _uiState.value = _uiState.value.copy(isEditing = isEditing)
    }
    
    fun setEditingAddress(address: Address?) {
        _uiState.value = _uiState.value.copy(editingAddress = address)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val editingAddress: Address? = null,
    val error: String? = null
)