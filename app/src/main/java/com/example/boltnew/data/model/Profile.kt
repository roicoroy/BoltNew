package com.example.boltnew.data.model

import java.time.LocalDate

data class Profile(
    val id: Int = 1,
    val documentId: String = "",
    val username: String,
    val email: String,
    val dateOfBirth: LocalDate,
    val addresses: List<Address> = emptyList(),
    val avatar: Avatar? = null,
    val userAdverts: List<UserAdvert> = emptyList(),
    val role: UserRole? = null,
    val isBlocked: Boolean = false,
    val isConfirmed: Boolean = true,
    val provider: String = "local"
) {
    val displayName: String
        get() = if (username.isNotBlank()) username else email.substringBefore("@")
    
    val primaryAddress: Address?
        get() = addresses.firstOrNull()
    
    val fullAddress: String
        get() = primaryAddress?.let { address ->
            buildString {
                append(address.firstLineAddress)
                if (address.secondLineAddress.isNotBlank()) {
                    append(", ${address.secondLineAddress}")
                }
                append(", ${address.city}")
                if (address.postCode.isNotBlank()) {
                    append(" ${address.postCode}")
                }
                append(", ${address.country}")
            }
        } ?: ""
}

data class Address(
    val id: Int = 0,
    val documentId: String = "",
    val firstName: String,
    val lastName: String,
    val firstLineAddress: String,
    val secondLineAddress: String = "",
    val city: String,
    val postCode: String,
    val country: String,
    val phoneNumber: String? = null
) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class Avatar(
    val id: Int = 0,
    val documentId: String = "",
    val name: String,
    val url: String,
    val alternativeText: String? = null,
    val caption: String? = null,
    val width: Int = 0,
    val height: Int = 0,
    val size: Double = 0.0,
    val formats: AvatarFormats? = null
)

data class AvatarFormats(
    val thumbnail: AvatarFormat? = null,
    val small: AvatarFormat? = null,
    val medium: AvatarFormat? = null,
    val large: AvatarFormat? = null
)

data class AvatarFormat(
    val name: String,
    val url: String,
    val width: Int,
    val height: Int,
    val size: Double
)

data class UserAdvert(
    val id: Int,
    val documentId: String,
    val title: String,
    val description: String,
    val slug: String? = null
)

data class UserRole(
    val id: Int,
    val documentId: String,
    val name: String,
    val description: String,
    val type: String
)