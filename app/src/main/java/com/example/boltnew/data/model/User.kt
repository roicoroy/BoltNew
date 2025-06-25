package com.example.boltnew.data.model

import java.time.LocalDate

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val dateOfBirth: LocalDate,
    val avatarPath: String?
) {
    val fullName: String
        get() = "$firstName $lastName"
}