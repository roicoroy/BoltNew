package com.example.boltnew.data.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthenticatedHttpClient(private val tokenManager: TokenManager) {
    
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
                prettyPrint = true
                coerceInputValues = true
            })
        }
        
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.INFO
        }
        
        install(Auth) {
            bearer {
                loadTokens {
                    val token = tokenManager.getToken()
                    if (token != null) {
                        BearerTokens(token, token)
                    } else {
                        null
                    }
                }
                
                refreshTokens {
                    val currentToken = tokenManager.getToken()
                    if (currentToken != null && !tokenManager.isTokenExpired()) {
                        // Token is still valid, refresh expiry
                        tokenManager.refreshTokenExpiry()
                        BearerTokens(currentToken, currentToken)
                    } else {
                        // Token expired or invalid, clear it
                        tokenManager.clearToken()
                        null
                    }
                }
                
                sendWithoutRequest { request ->
                    // Add token to all requests except auth endpoints
                    !request.url.encodedPath.contains("/auth/")
                }
            }
        }
        
        install(DefaultRequest) {
            header("ngrok-skip-browser-warning", "true")
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }
        
        // Handle authentication errors
        install(HttpCallValidator) {
            handleResponseExceptionWithRequest { exception, request ->
                when (exception) {
                    is ClientRequestException -> {
                        if (exception.response.status == HttpStatusCode.Unauthorized) {
                            // Token is invalid, clear it
                            tokenManager.clearToken()
                        }
                    }
                }
            }
        }
        
        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
    }
}