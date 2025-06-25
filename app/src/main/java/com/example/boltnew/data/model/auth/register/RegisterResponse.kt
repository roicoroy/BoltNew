package com.example.boltnew.data.model.auth.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
  "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNzQ5MzgxMjQ0LCJleHAiOjE3NTE5NzMyNDR9.Blyb77bBMRnHuxuCyJIRErApB8xmaCu4aij6IsBJx_A",
  "user": {
    "id": 1,
    "username": "Kapman",
    "email": "test@test.com",
    "provider": "local",
    "confirmed": true,
    "blocked": false,
    "createdAt": "2025-06-08T11:14:04.284Z",
    "updatedAt": "2025-06-08T11:14:04.284Z",
    "role": {
      "id": 1,
      "name": "Authenticated",
      "description": "Default role given to authenticated user.",
      "type": "authenticated",
      "createdAt": "2025-06-05T11:09:00.237Z",
      "updatedAt": "2025-06-08T11:09:12.817Z"
    }
  }
}
*/
@Serializable
data class RegisterResponse(
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
        @SerialName("role")
        val role: Role,
        @SerialName("updatedAt")
        val updatedAt: String,
        @SerialName("username")
        val username: String
    ) {
        @Serializable
        data class Role(
            @SerialName("createdAt")
            val createdAt: String,
            @SerialName("description")
            val description: String,
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("type")
            val type: String,
            @SerialName("updatedAt")
            val updatedAt: String
        )
    }
}