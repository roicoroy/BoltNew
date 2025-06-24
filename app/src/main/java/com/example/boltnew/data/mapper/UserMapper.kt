package com.example.boltnew.data.mapper

import com.example.boltnew.data.database.UserEntity
import com.example.boltnew.data.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        address = address,
        dateOfBirth = LocalDate.parse(dateOfBirth, dateFormatter),
        avatarPath = avatarPath
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        address = address,
        dateOfBirth = dateOfBirth.format(dateFormatter),
        avatarPath = avatarPath
    )
}