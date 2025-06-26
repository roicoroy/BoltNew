package com.example.boltnew.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boltnew.data.model.auth.profile.Profile
import com.example.boltnew.data.model.auth.user.User
import com.example.boltnew.data.repository.auth.AuthRepository
import com.example.boltnew.data.repository.profile.ProfileRepository
import com.example.boltnew.utils.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository // Add ProfileRepository for database cleanup
) : ViewModel() {
    
    private val _authState = MutableStateFlow<RequestState<Boolean>>(RequestState.Idle)
    val authState: StateFlow<RequestState<Boolean>> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<RequestState<User>>(RequestState.Idle)
    val currentUser: StateFlow<RequestState<User>> = _currentUser.asStateFlow()
    
    private val _userProfile = MutableStateFlow<RequestState<Profile>>(RequestState.Idle)
    val userProfile: StateFlow<RequestState<Profile>> = _userProfile.asStateFlow()
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // Observe authentication state from repository
    val isLoggedIn = authRepository.getAuthState()
    
    init {
        checkAuthenticationStatus()
    }
    
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                _authState.value = RequestState.Success(true)
                loadCurrentUser()
            } else {
                _authState.value = RequestState.Success(false)
            }
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = RequestState.Loading
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                val result = authRepository.login(email, password)
                
                if (result.isSuccess) {
                    _authState.value = RequestState.Success(true)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Login successful!"
                    )
                    loadCurrentUser()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Login failed"
                    _authState.value = RequestState.Error(error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            } catch (e: Exception) {
                val error = "Login failed: ${e.message}"
                _authState.value = RequestState.Error(error)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error
                )
            }
        }
    }
    
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = RequestState.Loading
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                val result = authRepository.register(username, email, password)
                
                if (result.isSuccess) {
                    _authState.value = RequestState.Success(true)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Registration successful!"
                    )
                    loadCurrentUser()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Registration failed"
                    _authState.value = RequestState.Error(error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
            } catch (e: Exception) {
                val error = "Registration failed: ${e.message}"
                _authState.value = RequestState.Error(error)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                println("🚪 AuthViewModel - Starting logout process...")
                
                // Step 1: Logout from server (clear server session)
                val token = authRepository.getCurrentToken()
                if (token != null) {
                    println("🌐 AuthViewModel - Logging out from server...")
                    val result = authRepository.logout()
                    if (result.isSuccess) {
                        println("✅ AuthViewModel - Server logout successful")
                    } else {
                        println("⚠️ AuthViewModel - Server logout failed, continuing with local cleanup")
                    }
                } else {
                    println("ℹ️ AuthViewModel - No token found, skipping server logout")
                }
                
                // Step 2: Clear local database data
                println("🗑️ AuthViewModel - Clearing local database...")
                try {
                    // Get current profile to delete it
                    profileRepository.getProfile().collect { profile ->
                        if (profile != null) {
                            println("🗑️ AuthViewModel - Deleting profile: ${profile.documentId}")
                            profileRepository.deleteProfile(profile)
                            println("✅ AuthViewModel - Profile deleted from local database")
                        } else {
                            println("ℹ️ AuthViewModel - No profile found in local database")
                        }
                        
                        // Break out of collect after first emission
                        return@collect
                    }
                } catch (e: Exception) {
                    println("⚠️ AuthViewModel - Failed to clear local database: ${e.message}")
                    // Continue with logout even if database cleanup fails
                }
                
                // Step 3: Clear authentication state and user data
                println("🧹 AuthViewModel - Clearing authentication state...")
                _authState.value = RequestState.Success(false)
                _currentUser.value = RequestState.Idle
                _userProfile.value = RequestState.Idle
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Logged out successfully",
                    errorMessage = null
                )
                
                println("✅ AuthViewModel - Logout process completed successfully")
                
            } catch (e: Exception) {
                println("💥 AuthViewModel - Logout error: ${e.message}")
                e.printStackTrace()
                
                // Even if there's an error, clear the local state
                _authState.value = RequestState.Success(false)
                _currentUser.value = RequestState.Idle
                _userProfile.value = RequestState.Idle
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Logout completed with some issues: ${e.message}"
                )
            }
        }
    }
    
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                _currentUser.value = RequestState.Loading
                
                val result = authRepository.getCurrentUser()
                
                if (result.isSuccess) {
                    _currentUser.value = RequestState.Success(result.getOrThrow())
                } else {
                    _currentUser.value = RequestState.Error(
                        result.exceptionOrNull()?.message ?: "Failed to load user"
                    )
                }
            } catch (e: Exception) {
                _currentUser.value = RequestState.Error("Failed to load user: ${e.message}")
            }
        }
    }
    
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _userProfile.value = RequestState.Loading
                
                val result = authRepository.getUserProfile()
                
                if (result.isSuccess()) {
                    _userProfile.value = RequestState.Success(result.getSuccessData())
                } else {
                    _userProfile.value = RequestState.Error(
                        result.getErrorMessage().toString() ?: "Failed to load profile"
                    )
                }
            } catch (e: Exception) {
                _userProfile.value = RequestState.Error("Failed to load profile: ${e.message}")
            }
        }
    }
    
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                val result = authRepository.forgotPassword(email)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Password reset email sent!"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to send reset email: ${e.message}"
                )
            }
        }
    }
    
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            try {
                if (newPassword != confirmPassword) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "New passwords do not match"
                    )
                    return@launch
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                val result = authRepository.changePassword(currentPassword, newPassword, confirmPassword)
                
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Password changed successfully!"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to change password"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to change password: ${e.message}"
                )
            }
        }
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
    
    fun refreshToken() {
        viewModelScope.launch {
            try {
                val result = authRepository.refreshToken()
                if (!result.isSuccess) {
                    // Token refresh failed, logout user
                    logout()
                }
            } catch (e: Exception) {
                // Token refresh failed, logout user
                logout()
            }
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)