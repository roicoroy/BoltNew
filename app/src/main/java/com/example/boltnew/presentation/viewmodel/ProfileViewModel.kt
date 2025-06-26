package com.example.boltnew.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.StrapiCategoryOption
import com.example.boltnew.data.model.auth.profile.Address
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.profile.UserAdvert
import com.example.boltnew.data.repository.auth.AuthRepository
import com.example.boltnew.data.repository.profile.ProfileRepository
import com.example.boltnew.data.repository.profile.ProfileRepositoryImpl
import com.example.boltnew.data.repository.address.AddressRepository
import com.example.boltnew.data.repository.user.UserAdvertRepository
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
    private val profileRepository: ProfileRepository,
    private val addressRepository: AddressRepository,
    private val userAdvertRepository: UserAdvertRepository
) : ViewModel() {
    
    private val _profileState = MutableStateFlow<RequestState<Profile>>(RequestState.Idle)
    val profileState: StateFlow<RequestState<Profile>> = _profileState.asStateFlow()
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val _categories = MutableStateFlow<List<StrapiCategoryOption>>(emptyList())
    val categories: StateFlow<List<StrapiCategoryOption>> = _categories.asStateFlow()
    
    init {
        loadUserProfile()
        loadCategories()
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _profileState.value = RequestState.Loading
                println("Loading user profile...")
                
                // First try to get profile from auth repository (Strapi API)
                val authResult = authRepository.getUserProfile()
                
                if (authResult.isSuccess()) {
                    val profile = authResult.getSuccessData()
                    println("Profile loaded successfully from API: $profile")
                    _profileState.value = RequestState.Success(profile)
                    
                    // Cache the profile locally
                    try {
                        profileRepository.insertProfile(profile)
                        println("Profile cached locally")
                    } catch (e: Exception) {
                        println("Failed to cache profile: ${e.message}")
                    }
                } else {
                    val apiError = authResult.getErrorMessage()
                    println("API failed: ${apiError}")
                    
                    // Fallback to local profile repository
                    println("Falling back to local profile...")
                    profileRepository.getProfile()
                        .onStart { 
                            if (_profileState.value !is RequestState.Loading) {
                                _profileState.value = RequestState.Loading
                            }
                        }
                        .catch { exception ->
                            println("Local profile error: ${exception.message}")
                            _profileState.value = RequestState.Error("Failed to load profile: ${exception.message}")
                        }
                        .collect { profile ->
                            if (profile != null) {
                                println("Profile loaded from local cache: $profile")
                                _profileState.value = RequestState.Success(profile)
                            } else {
                                println("No profile found locally")
                                _profileState.value = RequestState.Error("Profile not found. Please ensure you're logged in and have a complete profile.")
                            }
                        }
                }
            } catch (e: Exception) {
                println("Profile loading error: ${e.message}")
                e.printStackTrace()
                _profileState.value = RequestState.Error("Failed to load profile: ${e.message}")
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val result = userAdvertRepository.getCategories()
                if (result.isSuccess) {
                    _categories.value = result.getOrThrow()
                    println("✅ Categories loaded: ${_categories.value.size}")
                } else {
                    println("❌ Failed to load categories: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("💥 Categories loading error: ${e.message}")
            }
        }
    }
    
    fun updateAvatar(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isUpdatingAvatar = true)
                
                // Get current profile to extract documentId
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isUpdatingAvatar = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("🔄 Updating avatar for profile: ${currentProfile.documentId}")
                
                // Use ProfileRepositoryImpl to upload and update avatar
                if (profileRepository is ProfileRepositoryImpl) {
                    val result = profileRepository.uploadAndUpdateAvatar(
                        context = context,
                        imageUri = imageUri,
                        profileDocumentId = currentProfile.documentId
                    )
                    
                    if (result.isSuccess) {
                        val newAvatarUrl = result.getOrThrow()
                        _uiState.value = _uiState.value.copy(
                            isUpdatingAvatar = false,
                            operationMessage = "Avatar updated successfully!"
                        )
                        
                        println("✅ Avatar updated successfully: $newAvatarUrl")
                        
                        // Reload profile to show updated avatar
                        loadUserProfile()
                    } else {
                        val error = result.exceptionOrNull()?.message ?: "Unknown error"
                        _uiState.value = _uiState.value.copy(
                            isUpdatingAvatar = false,
                            operationMessage = "Failed to update avatar: $error"
                        )
                        println("❌ Avatar update failed: $error")
                    }
                } else {
                    // Fallback to old method if not using ProfileRepositoryImpl
                    val savedPath = com.example.boltnew.utils.ImageUtils.saveImageToInternalStorage(context, imageUri)
                    profileRepository.updateAvatar(savedPath, savedPath)
                    
                    _uiState.value = _uiState.value.copy(
                        isUpdatingAvatar = false,
                        operationMessage = "Avatar updated locally"
                    )
                    
                    // Reload profile to show updated avatar
                    loadUserProfile()
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdatingAvatar = false,
                    operationMessage = "Failed to update avatar: ${e.message}"
                )
                println("💥 Avatar update error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    // Profile DOB update
    fun updateProfileDob(dateOfBirth: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isProfileLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isProfileLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("📅 Updating profile DOB for: ${currentProfile.documentId}")
                
                val result = profileRepository.updateProfileDob(
                    profileDocumentId = currentProfile.documentId,
                    dateOfBirth = dateOfBirth
                )
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isProfileLoading = false,
                        operationMessage = "Date of birth updated successfully!",
                        showProfileModal = false
                    )
                    
                    println("✅ Profile DOB updated successfully")
                    
                    // Reload profile to show updated DOB
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isProfileLoading = false,
                        operationMessage = "Failed to update date of birth: $error"
                    )
                    println("❌ Profile DOB update failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isProfileLoading = false,
                    operationMessage = "Failed to update date of birth: ${e.message}"
                )
                println("💥 Profile DOB update error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    // Address CRUD operations
    fun createAddress(address: Address) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAddressLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("🏠 Creating new address for profile: ${currentProfile.documentId}")
                
                val result = addressRepository.createAddress(address, currentProfile.documentId)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Address created successfully!",
                        showAddressModal = false
                    )
                    
                    println("✅ Address created successfully")
                    
                    // Reload profile to show new address
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Failed to create address: $error"
                    )
                    println("❌ Address creation failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddressLoading = false,
                    operationMessage = "Failed to create address: ${e.message}"
                )
                println("💥 Address creation error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun updateAddress(address: Address) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAddressLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("🔄 Updating address: ${address.documentId}")
                
                val result = addressRepository.updateAddress(address, currentProfile.documentId)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Address updated successfully!",
                        showAddressModal = false,
                        editingAddress = null
                    )
                    
                    println("✅ Address updated successfully")
                    
                    // Reload profile to show updated address
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Failed to update address: $error"
                    )
                    println("❌ Address update failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddressLoading = false,
                    operationMessage = "Failed to update address: ${e.message}"
                )
                println("💥 Address update error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun deleteAddress(address: Address) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAddressLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("🗑️ Deleting address: ${address.documentId}")
                
                val result = addressRepository.deleteAddress(address.documentId, currentProfile.documentId)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Address deleted successfully!"
                    )
                    
                    println("✅ Address deleted successfully")
                    
                    // Reload profile to remove deleted address
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isAddressLoading = false,
                        operationMessage = "Failed to delete address: $error"
                    )
                    println("❌ Address deletion failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAddressLoading = false,
                    operationMessage = "Failed to delete address: ${e.message}"
                )
                println("💥 Address deletion error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    // Advert CRUD operations
    fun createAdvert(
        context: Context,
        title: String,
        description: String,
        slug: String,
        categoryId: Int,
        coverImageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAdvertLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("📝 Creating new advert for profile: ${currentProfile.documentId}")
                
                val result = userAdvertRepository.createAdvert(
                    context = context,
                    title = title,
                    description = description,
                    slug = slug,
                    categoryId = categoryId,
                    coverImageUri = coverImageUri,
                    profileDocumentId = currentProfile.documentId
                )
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Advert created successfully!",
                        showAdvertModal = false
                    )
                    
                    println("✅ Advert created successfully")
                    
                    // Reload profile to show new advert
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Failed to create advert: $error"
                    )
                    println("❌ Advert creation failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAdvertLoading = false,
                    operationMessage = "Failed to create advert: ${e.message}"
                )
                println("💥 Advert creation error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun updateAdvert(
        context: Context,
        advert: UserAdvert,
        title: String,
        description: String,
        slug: String,
        categoryId: Int,
        coverImageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAdvertLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("🔄 Updating advert: ${advert.documentId}")
                
                val result = userAdvertRepository.updateAdvert(
                    context = context,
                    advert = advert,
                    title = title,
                    description = description,
                    slug = slug,
                    categoryId = categoryId,
                    coverImageUri = coverImageUri,
                    profileDocumentId = currentProfile.documentId
                )
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Advert updated successfully!",
                        showAdvertModal = false,
                        editingAdvert = null
                    )
                    
                    println("✅ Advert updated successfully")
                    
                    // Reload profile to show updated advert
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Failed to update advert: $error"
                    )
                    println("❌ Advert update failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAdvertLoading = false,
                    operationMessage = "Failed to update advert: ${e.message}"
                )
                println("💥 Advert update error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    fun deleteAdvert(advert: UserAdvert) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isAdvertLoading = true)
                
                val currentProfile = (_profileState.value as? RequestState.Success)?.data
                if (currentProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Profile not loaded. Please try again."
                    )
                    return@launch
                }
                
                println("🗑️ Deleting advert: ${advert.documentId}")
                
                val result = userAdvertRepository.deleteAdvert(advert.documentId, currentProfile.documentId)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Advert deleted successfully!"
                    )
                    
                    println("✅ Advert deleted successfully")
                    
                    // Reload profile to remove deleted advert
                    loadUserProfile()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Unknown error"
                    _uiState.value = _uiState.value.copy(
                        isAdvertLoading = false,
                        operationMessage = "Failed to delete advert: $error"
                    )
                    println("❌ Advert deletion failed: $error")
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAdvertLoading = false,
                    operationMessage = "Failed to delete advert: ${e.message}"
                )
                println("💥 Advert deletion error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    
    // UI state management for profile
    fun showProfileEditModal() {
        _uiState.value = _uiState.value.copy(showProfileModal = true)
    }
    
    fun hideProfileEditModal() {
        _uiState.value = _uiState.value.copy(showProfileModal = false)
    }
    
    // UI state management for addresses
    fun showAddAddressModal() {
        _uiState.value = _uiState.value.copy(
            showAddressModal = true,
            editingAddress = null
        )
    }
    
    fun showEditAddressModal(address: Address) {
        _uiState.value = _uiState.value.copy(
            showAddressModal = true,
            editingAddress = address
        )
    }
    
    fun hideAddressModal() {
        _uiState.value = _uiState.value.copy(
            showAddressModal = false,
            editingAddress = null
        )
    }
    
    // UI state management for adverts
    fun showAddAdvertModal() {
        _uiState.value = _uiState.value.copy(
            showAdvertModal = true,
            editingAdvert = null
        )
    }
    
    fun showEditAdvertModal(advert: UserAdvert) {
        _uiState.value = _uiState.value.copy(
            showAdvertModal = true,
            editingAdvert = advert
        )
    }
    
    fun hideAdvertModal() {
        _uiState.value = _uiState.value.copy(
            showAdvertModal = false,
            editingAdvert = null
        )
    }
    
    fun refreshProfile() {
        println("Refreshing profile...")
        loadUserProfile()
        loadCategories()
    }
    
    fun clearOperationMessage() {
        _uiState.value = _uiState.value.copy(operationMessage = null)
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    fun retryLoadProfile() {
        println("Retrying profile load...")
        loadUserProfile()
    }
}

data class ProfileUiState(
    val operationMessage: String? = null,
    val isUpdatingAvatar: Boolean = false,
    val isProfileLoading: Boolean = false,
    val showProfileModal: Boolean = false,
    val isAddressLoading: Boolean = false,
    val showAddressModal: Boolean = false,
    val editingAddress: Address? = null,
    val isAdvertLoading: Boolean = false,
    val showAdvertModal: Boolean = false,
    val editingAdvert: UserAdvert? = null
)