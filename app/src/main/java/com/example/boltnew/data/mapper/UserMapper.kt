package com.example.boltnew.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.boltnew.data.database.UserEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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