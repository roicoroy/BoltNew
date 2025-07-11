package com.example.boltnew.data.repository.user

import android.content.Context
import android.net.Uri
import com.example.boltnew.data.model.StrapiCategoryOption
import com.example.boltnew.data.model.auth.profile.UserAdvert

interface UserAdvertRepository {
    suspend fun createAdvert(
        context: Context,
        title: String,
        description: String,
        slug: String,
        categoryId: Int,
        coverImageUri: Uri?,
        profileDocumentId: String
    ): Result<UserAdvert>
    
    suspend fun updateAdvert(
        context: Context,
        advert: UserAdvert,
        title: String,
        description: String,
        slug: String,
        categoryId: Int,
        coverImageUri: Uri?,
        profileDocumentId: String
    ): Result<UserAdvert>
    
    suspend fun deleteAdvert(
        advertDocumentId: String,
        profileDocumentId: String
    ): Result<Boolean>
    
    suspend fun getCategories(): Result<List<StrapiCategoryOption>>
}