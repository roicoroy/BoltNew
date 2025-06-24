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
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateProfile(
        username: String,
        email: String,
        dateOfBirth: LocalDate,
        addresses: List<Address> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                val currentState = _profileState.value
                if (currentState is RequestState.Success) {
                    val currentProfile = currentState.data
                    val updatedProfile = currentProfile.copy(
                        username = username,
                        email = email,
                        dateOfBirth = dateOfBirth,
                        addresses = addresses.ifEmpty { currentProfile.addresses }
                    )
                    
                    profileRepository.updateProfile(updatedProfile)
                    
                    _uiState.value = _uiState.value.copy(
                        isEditing = false,
                        operationMessage = "Profile updated successfully"
                    )
                } else {
                    val newProfile = Profile(
                        username = username,
                        email = email,
                        dateOfBirth = dateOfBirth,
                        addresses = addresses
                    )
                    
                    profileRepository.saveProfile(newProfile)
                    
                    _uiState.value = _uiState.value.copy(
                        isEditing = false,
                        operationMessage = "Profile created successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Failed to update profile: ${e.message}"
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
                    operationMessage = "Avatar updated successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Failed to update avatar: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun addAddress(address: Address) {
        viewModelScope.launch {
            try {
                val currentState = _profileState.value
                if (currentState is RequestState.Success) {
                    val currentProfile = currentState.data
                    val updatedAddresses = currentProfile.addresses + address
                    val updatedProfile = currentProfile.copy(addresses = updatedAddresses)
                    profileRepository.updateProfile(updatedProfile)
                    
                    _uiState.value = _uiState.value.copy(
                        operationMessage = "Address added successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Failed to add address: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAddress(address: Address) {
        viewModelScope.launch {
            try {
                val currentState = _profileState.value
                if (currentState is RequestState.Success) {
                    val currentProfile = currentState.data
                    val updatedAddresses = currentProfile.addresses.map { 
                        if (it.id == address.id) address else it 
                    }
                    val updatedProfile = currentProfile.copy(addresses = updatedAddresses)
                    profileRepository.updateProfile(updatedProfile)
                    
                    _uiState.value = _uiState.value.copy(
                        operationMessage = "Address updated successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Failed to update address: ${e.message}"
                )
            }
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            try {
                val currentState = _profileState.value
                if (currentState is RequestState.Success) {
                    val currentProfile = currentState.data
                    val updatedAddresses = currentProfile.addresses.filter { it.id != address.id }
                    val updatedProfile = currentProfile.copy(addresses = updatedAddresses)
                    profileRepository.updateProfile(updatedProfile)
                    
                    _uiState.value = _uiState.value.copy(
                        operationMessage = "Address deleted successfully"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    operationMessage = "Failed to delete address: ${e.message}"
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
    
    fun clearOperationMessage() {
        _uiState.value = _uiState.value.copy(operationMessage = null)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun retryLoadProfile() {
        loadProfile()
    }
}

data class ProfileUiState(
    val isEditing: Boolean = false,
    val editingAddress: Address? = null,
    val operationMessage: String? = null
)