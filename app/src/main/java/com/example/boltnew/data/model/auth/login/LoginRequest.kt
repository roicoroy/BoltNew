package com.example.boltnew.data.model.auth.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
  "identifier": "test@test.com",
  "password": "Password"
}
*/
@Serializable
data class LoginRequest(
    @SerialName("identifier")
    val identifier: String,
    @SerialName("password")
    val password: String
)