package com.example.boltnew.data.model.auth.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
  "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNzQ5Mzg0MzMyLCJleHAiOjE3NTE5NzYzMzJ9.z_txCpXSgr9jTznmOg_afLvv4qwqPe7ZDuS84xuscLY",
  "user": {
    "id": 1,
    "username": "Kapman",
    "email": "test@test.com",
    "provider": "local",
    "confirmed": true,
    "blocked": false,
    "createdAt": "2025-06-08T11:14:04.284Z",
    "updatedAt": "2025-06-08T11:14:04.284Z"
  }
}
*/
@Serializable
data class LoginResponse(
    @SerialName("jwt")
    val jwt: String,
    @SerialName("user")
    val user: User
) {
    @Serializable
    data class User(
        @SerialName("blocked")
        val blocked: Boolean,
        @SerialName("confirmed")
        val confirmed: Boolean,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("email")
        val email: String,
        @SerialName("id")
        val id: Int,
        @SerialName("provider")
        val provider: String,
        @SerialName("updatedAt")
        val updatedAt: String,
        @SerialName("username")
        val username: String
    )
}