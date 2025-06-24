package com.example.boltnew.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey
    val id: Int = 1, // Single user profile
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val dateOfBirth: String, // Stored as ISO date string
    val avatarPath: String? = null
)