package com.example.boltnew.data.model.auth.register


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
  "username": "Ricardo",
  "email": "test@test.com",
  "password": "Password"
}
*/
@Serializable
data class RegisterRequest(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("username")
    val username: String
)