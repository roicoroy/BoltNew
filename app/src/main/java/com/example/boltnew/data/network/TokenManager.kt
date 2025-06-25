package com.example.boltnew.data.network

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TokenManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "auth_prefs", 
        Context.MODE_PRIVATE
    )
    
    private val _authState = MutableStateFlow(getStoredToken() != null)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()
    
    private val _currentToken = MutableStateFlow(getStoredToken())
    val currentToken: StateFlow<String?> = _currentToken.asStateFlow()
    
    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
    
    fun saveToken(token: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (24 * 60 * 60 * 1000)) // 24 hours
            .apply()
        _currentToken.value = token
        _authState.value = true
    }
    
    fun getToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)
        val expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        
        return if (token != null && System.currentTimeMillis() < expiry) {
            token
        } else {
            if (token != null) {
                clearToken() // Clear expired token
            }
            null
        }
    }
    
    private fun getStoredToken(): String? {
        return getToken()
    }
    
    fun saveUserInfo(userId: Int, username: String, email: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_EMAIL, email)
            .apply()
    }
    
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }
    
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }
    
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }
    
    fun clearToken() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_EMAIL)
            .remove(KEY_TOKEN_EXPIRY)
            .apply()
        _currentToken.value = null
        _authState.value = false
    }
    
    fun isTokenExpired(): Boolean {
        val expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        return System.currentTimeMillis() >= expiry
    }
    
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
    
    fun refreshTokenExpiry() {
        if (getToken() != null) {
            prefs.edit()
                .putLong(KEY_TOKEN_EXPIRY, System.currentTimeMillis() + (24 * 60 * 60 * 1000))
                .apply()
        }
    }
}