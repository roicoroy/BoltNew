package com.example.boltnew.data.model.auth.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StrapiUser(
    @SerialName("blocked")
    val blocked: Boolean = false,
    @SerialName("confirmed")
    val confirmed: Boolean = false,
    @SerialName("createdAt")
    val createdAt: String = "",
    @SerialName("documentId")
    val documentId: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("id")
    val id: Int = 0,
    @SerialName("profile")
    val profile: Profile = Profile(),
    @SerialName("provider")
    val provider: String = "",
    @SerialName("publishedAt")
    val publishedAt: String = "",
    @SerialName("role")
    val role: Role = Role(),
    @SerialName("updatedAt")
    val updatedAt: String = "",
    @SerialName("username")
    val username: String = ""
) {
    @Serializable
    data class Profile(
        @SerialName("createdAt")
        val createdAt: String = "",
        @SerialName("dob")
        val dob: String = "",
        @SerialName("documentId")
        val documentId: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("publishedAt")
        val publishedAt: String = "",
        @SerialName("updatedAt")
        val updatedAt: String = ""
    )

    @Serializable
    data class Role(
        @SerialName("createdAt")
        val createdAt: String = "",
        @SerialName("description")
        val description: String = "",
        @SerialName("documentId")
        val documentId: String = "",
        @SerialName("id")
        val id: Int = 0,
        @SerialName("name")
        val name: String = "",
        @SerialName("publishedAt")
        val publishedAt: String = "",
        @SerialName("type")
        val type: String = "",
        @SerialName("updatedAt")
        val updatedAt: String = ""
    )
}