package com.example.boltnew.data.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClient {
    
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
        
        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }
    }
}